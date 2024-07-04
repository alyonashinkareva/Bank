package info.kgeorgiy.ja.shinkareva.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;

/**
 * @author Shinkareva Alyona (alyona.i.shinkareva@gmail.com)
 */

public class BankTests {
    private static Bank bank;
    private static Registry registry;
    private static final int DEFAULT_BANK_PORT = 8888;
    private static final int DEFAULT_REGISTRY_PORT = 1099;
    private static final String HOST_NAME = "//localhost/bank";
    private static final String DEFAULT_NAME = "defName";
    private static final String DEFAULT_SURNAME = "defSurname";
    private static final String DEFAULT_PASSPORT = "defPassport";
    private static final String DEFAULT_ID = "007";
    private static final int ZERO_AMOUNT = 0;
    private static final int DEFAULT_AMOUNT = 2;
    private static final int LIMIT = 15;

    @BeforeAll
    static void beforeAll() throws RemoteException {
        registry = LocateRegistry.createRegistry(DEFAULT_REGISTRY_PORT);
    }

    @BeforeEach
    void beforeEach() throws RemoteException, NotBoundException {
        registry.rebind(HOST_NAME, new RemoteBank(DEFAULT_BANK_PORT));
        bank = (Bank) registry.lookup(HOST_NAME);
        UnicastRemoteObject.exportObject(bank, DEFAULT_BANK_PORT);
    }

    @Test
    public void createAndGetPersonTest() throws RemoteException {
        Assertions.assertNull(bank.getPersonByPassport("no_such_passport", Person.PersonType.REMOTE));
        Assertions.assertNull(bank.getPersonByPassport("no_such_passport", Person.PersonType.LOCAL));

        for (int i = 0; i < LIMIT; i++) {
            String name = createName(String.valueOf(i));
            String surname = createSurname(String.valueOf(i));
            String passport = createPassport(String.valueOf(i));


            Person remotePerson = createAndGetPerson(name, surname, passport, Person.PersonType.REMOTE);
            checkPerson(name, surname, passport, remotePerson);
            Person localPerson = createAndGetPerson(name, surname, passport, Person.PersonType.LOCAL);
            checkPerson(name, surname, passport, localPerson);
        }
    }

    @Test
    public void createAndGetAccountsTest() throws RemoteException{
        for (int i = 0; i < LIMIT; i++) {
            Person person = createAndGetPerson(String.valueOf(i), String.valueOf(i), String.valueOf(i), Person.PersonType.REMOTE);
            for (int j = 0; j < LIMIT; j++) {
                createAndGetAccount(String.valueOf(j), person);
            }
            var accounts = person.getAccounts();
            Assertions.assertNotNull(accounts);
            Assertions.assertEquals(LIMIT, accounts.size());
        }
    }

    @Test
    public void amountTest() throws RemoteException {
        Person remotePerson = createAndGetPerson(DEFAULT_NAME, DEFAULT_SURNAME, DEFAULT_PASSPORT, Person.PersonType.REMOTE);
        for (int i = 0; i < LIMIT; i++) {
            createAndGetAccount(String.valueOf(i), remotePerson);
        }
        Person localPerson = new LocalPerson((RemotePerson) remotePerson);
        setAmountOfAccounts(remotePerson.getAllAccounts(), DEFAULT_AMOUNT);
        for (var account : remotePerson.getAllAccounts()) {
            checkAmountsEquality(account, localPerson.getAccounts().get(getFullAccountId(localPerson.getPassport(), account.getId())), false);
        }
    }

    @Test
    public void localAndRemoteTest() throws RemoteException {
        Person remotePerson = createAndGetPerson(DEFAULT_NAME, DEFAULT_SURNAME, DEFAULT_PASSPORT, Person.PersonType.REMOTE);
        Account remoteAccount = createAndGetAccount(DEFAULT_ID, remotePerson);
        Person localPerson1 = new LocalPerson((RemotePerson) remotePerson);
        try {
            remoteAccount.setAmount(DEFAULT_AMOUNT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Person localPerson2 = new LocalPerson((RemotePerson) remotePerson);
        Account localAccount1 = localPerson1.getAccounts().get(getFullAccountId(DEFAULT_PASSPORT, DEFAULT_ID));
        Account localAccount2 = localPerson2.getAccounts().get(getFullAccountId(DEFAULT_PASSPORT, DEFAULT_ID));
        checkAmountsEquality(remoteAccount, localAccount1, false);
        checkAmountsEquality(remoteAccount, localAccount2, true);
        checkAmountsEquality(localAccount1, localAccount2, false);
    }

    @Test
    public void changeAmountTest() throws Exception {
        Person remotePerson = createAndGetPerson(DEFAULT_NAME, DEFAULT_SURNAME, DEFAULT_PASSPORT, Person.PersonType.REMOTE);
        Account remoteAccount = createAndGetAccount(DEFAULT_ID, remotePerson);
        Assertions.assertEquals(ZERO_AMOUNT, remoteAccount.getAmount());
        remoteAccount.setAmount(DEFAULT_AMOUNT);
        Assertions.assertEquals(DEFAULT_AMOUNT, remoteAccount.getAmount());
    }

    @Test
    public void createManyAccountsTest() throws RemoteException {
        Person remotePerson = createAndGetPerson(DEFAULT_NAME, DEFAULT_SURNAME, DEFAULT_PASSPORT, Person.PersonType.REMOTE);
        for (int j = 0; j < LIMIT; j++) {
            Assertions.assertNotNull(bank.createAccount(String.valueOf(j), remotePerson));
        }
        Assertions.assertNotNull(remotePerson.getAccounts());
        for (int j = 0; j < LIMIT; j++) {
            Assertions.assertNotNull(bank.getAccount(getFullAccountId(remotePerson.getPassport(), String.valueOf(j))));
        }
    }

    @Test
    public void clientMain() throws Exception {
        for (int i = 0; i < LIMIT / 2; i++) {
            String name = createName(String.valueOf(i));
            String surname = createSurname(String.valueOf(i));
            String passport = createPassport(String.valueOf(i));
            String subId = String.valueOf(i);
            int amount = i * 100;
            Person remotePerson = createAndGetPerson(name, surname, passport, Person.PersonType.REMOTE);
            Account remoteAccount = createAndGetAccount(subId, remotePerson);
            Assertions.assertEquals(ZERO_AMOUNT, remoteAccount.getAmount());
            Client.main(name, surname, passport, subId, String.valueOf(amount));
            Assertions.assertEquals(amount, bank.getAccount(getFullAccountId(remotePerson.getPassport(), subId)).getAmount());
            System.out.println();
        }
    }

    private void checkPerson(String name, String surname, String passport, Person person) throws RemoteException {
        Assertions.assertEquals(name, person.getName());
        Assertions.assertEquals(surname, person.getSurname());
        Assertions.assertEquals(passport, person.getPassport());
    }

    private String createName(String suffix) {
        return "name_" + suffix;
    }

    private String createSurname(String suffix) {
        return "surname_" + suffix;
    }

    private String createPassport(String suffix) {
        return "passport_" + suffix;
    }

    private String getFullAccountId(String passport, String subId) {
        return passport + ":" + subId;
    }

    private Person createAndGetPerson(String name, String surname, String passport, Person.PersonType personType) throws RemoteException {
        Assertions.assertNotNull(bank.createPerson(name, surname, passport));
        return bank.getPersonByPassport(passport, personType);
    }

    private Account createAndGetAccount(String id, Person person) throws RemoteException {
        Assertions.assertNotNull(bank.createAccount(id, person));
        return bank.getAccount(getFullAccountId(person.getPassport(), id));
    }

    private void checkAmountsEquality(Account firstAccount, Account secondAccount, boolean mustBeEqual) throws RemoteException {
        if (mustBeEqual) {
            Assertions.assertEquals(firstAccount.getAmount(), secondAccount.getAmount());
        } else {
            Assertions.assertNotEquals(firstAccount.getAmount(), secondAccount.getAmount());
        }
    }

    private void setAmountOfAccounts(Set<Account> accounts, int amount) {
        for (var account : accounts) {
            try {
                account.setAmount(amount);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
