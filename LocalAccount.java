package info.kgeorgiy.ja.shinkareva.bank;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * Class implements {@link Account} interface.
 * @author Shinkareva Alyona (alyona.i.shinkareva@gmail.com)
 */

public class LocalAccount implements Account, Serializable {
    private final String id;
    private int amount; // :NOTE: мало

    LocalAccount(RemoteAccount account) {
        this.id = account.getId();
        this.amount = account.getAmount();
    }

    /** Returns account identifier. */

    @Override
    public String getId() throws RemoteException {
        return id;
    }

    /** Returns amount of money in the account. */

    @Override
    public int getAmount() throws RemoteException {
        return amount;
    }

    /**
     * Sets amount of money in the account.
     * @param amount amount to set.
     */

    @Override
    public void setAmount(int amount) throws Exception {
        if (amount < 0) {
            throw new Exception("Amount can't be less then 0");
        }
        this.amount = amount;
    }
}
