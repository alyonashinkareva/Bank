package info.kgeorgiy.ja.shinkareva.bank;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class implements {@link Account} interface.
 * @author Shinkareva Alyona (alyona.i.shinkareva@gmail.com)
 */

public class RemotePerson implements Person{
    private final String name;
    private final String surname;
    private final String passport;
    private final Map<String, Account> accounts;

    RemotePerson(String name, String surname, String passport, Map<String, Account> accounts) {
        this.name = name;
        this.surname = surname;
        this.passport = passport;
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
    public Map<String, Account> getAccounts() throws RemoteException { // :NOTE: то-то снаружи может добавить
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
