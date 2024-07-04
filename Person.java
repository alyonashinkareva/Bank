package info.kgeorgiy.ja.shinkareva.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

/**
 * {@link Remote} interface for implementing {@link LocalPerson} and {@link RemotePerson}.
 * @author Shinkareva Alyona (alyona.i.shinkareva@gmail.com)
 */
public interface Person extends Remote {
    /** Returns person's name.*/
    String getName() throws RemoteException;

    /** Returns person's surname.*/
    String getSurname() throws RemoteException;

    /** Returns person's passport.*/
    String getPassport() throws RemoteException;

    /** Returns map of all person's accounts: account's id - account.*/

    Map<String, Account> getAccounts() throws RemoteException;

    /** Returns all person's accounts.*/

    Set<Account> getAllAccounts() throws RemoteException;

    /** Person's type: may be local or remote.*/
    enum PersonType {
        LOCAL,
        REMOTE
    }
}
