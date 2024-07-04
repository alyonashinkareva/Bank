package info.kgeorgiy.ja.shinkareva.bank;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Class implements {@link Bank} interface.
 * @author Shinkareva Alyona (alyona.i.shinkareva@gmail.com)
 */

public class RemoteBank implements Bank {
    private final int port;
    private final ConcurrentMap<String, RemotePerson> persons = new ConcurrentHashMap<>();

    public RemoteBank(final int port) {
        this.port = port;
    }

    /**
     * Creates a new account with specified identifier if it does not already exist.
     * @param subId account id
     * @return created or existing account.
     */

    @Override
    public Account createAccount(final String subId, Person person) throws RemoteException {
        if (subId != null && person != null) {
            String accountId = getFullAccountId(person.getPassport(), subId);
            final Account account = new RemoteAccount(subId);
            if (person.getAccounts().putIfAbsent(accountId, account) == null) {
                UnicastRemoteObject.exportObject(account, port);
                return account;
            } else {
                return getAccount(accountId);
            }
        } else {
            return null;
        }
    }

    /**
     * Returns account by identifier.
     * @param id account id
     * @return account with specified identifier or {@code null} if such account does not exist.
     */

    @Override
    public Account getAccount(final String id) throws RemoteException {
        if (id == null) {
            return null;
        }
        StringBuilder sbId = new StringBuilder(id);
        String passport = sbId.substring(0, sbId.indexOf(":"));
        return persons.get(passport).getAccounts().get(id);
    }

    /**
     * Creates a new person with specified name, surname, passport if it does not already exist.
     * @param name name
     * @param surname surname
     * @param passport passport
     * @return created or existing person.
     */

    @Override
    public Person createPerson(String name, String surname, String passport) throws RemoteException {
        if (name != null && surname != null && passport != null) {
            final RemotePerson person = new RemotePerson(name, surname, passport, new ConcurrentHashMap<>());
            if (persons.putIfAbsent(passport, person) == null) {
                UnicastRemoteObject.exportObject(person, port);
                return person;
            } else {
                return getPersonByPassport(passport, Person.PersonType.REMOTE);
            }
        } else {
            return null;
        }
    }

    /**
     * Returns person by passport.
     * @param passport passport
     * @param personType type od person: local or remote.
     * @return person with specified passport or {@code null} if such person does not exist.
     */

    @Override
    public Person getPersonByPassport(String passport, Person.PersonType personType) throws RemoteException {
        if (passport != null) {
            var p = persons.get(passport);
            return switch (personType) {
                case REMOTE -> p;
                case LOCAL -> {
                    if (p != null) {
                        yield new LocalPerson( p);
                    } else yield null;
                }
            };
        } else {
            return null;
        }
    }

    /**
     * Return all person's accounts.
     * @param person person
     * @return set of all person's accounts
     */

    @Override
    public Set<Account> getPersonsAccounts(Person person) throws RemoteException {
        if (person != null) {
            return person.getAllAccounts();
        } else {
            return null;
        }
    }

    private static String getFullAccountId(String passport, String subId) {
        return passport + ":" + subId;
    }
}
