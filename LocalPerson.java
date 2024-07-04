package info.kgeorgiy.ja.shinkareva.bank;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Class implements {@link Person} interface.
 * @author Shinkareva Alyona (alyona.i.shinkareva@gmail.com)
 */

public class LocalPerson implements Person, Serializable {
    private final String name;
    private final String surname;
    private final String passport;
    private final Map<String, Account> accounts;


    public LocalPerson(RemotePerson person) throws RemoteException {
        this.name = person.getName();
        this.surname = person.getSurname();
        this.passport = person.getPassport();
        Map<String, Account> accounts = new ConcurrentHashMap<>();
//        Map<String, Account> rpAccounts = person.getAccounts();
        for (var entry : person.getAccounts().entrySet()) {
            accounts.put(entry.getKey(), new LocalAccount((RemoteAccount) entry.getValue()));
        }
        this.accounts = accounts;
    }

    /** Returns person's name.*/

    @Override
    public String getName() throws RemoteException {
        return name;
    }

    /** Returns person's surname.*/

    @Override
    public String getSurname() throws RemoteException {
        return surname;
    }

    /** Returns person's passport.*/

    @Override
    public String getPassport() throws RemoteException {
        return passport;
    }

    /** Returns map of all person's accounts: account's id - account.*/

    @Override
    public Map<String, Account> getAccounts() throws RemoteException {
        return accounts;
    }

    /** Returns all person's accounts.*/

    @Override
    public Set<Account> getAllAccounts() throws RemoteException {
        Set<Account> result = new HashSet<>();
        for (var entry : accounts.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }
}
