package info.kgeorgiy.ja.shinkareva.bank;

import java.rmi.*;

/**
 * {@link Remote} interface for implementing {@link LocalAccount} and {@link RemoteAccount}.
 * @author Shinkareva Alyona (alyona.i.shinkareva@gmail.com)
 */

public interface Account extends Remote {
    /** Returns account identifier. */
    String getId() throws RemoteException;

    /** Returns amount of money in the account. */
    int getAmount() throws RemoteException;

    /** Sets amount of money in the account. */
    void setAmount(int amount) throws Exception;
}
