package info.kgeorgiy.ja.shinkareva.bank;

/**
 * Class implements {@link Account} interface.
 * @author Shinkareva Alyona (alyona.i.shinkareva@gmail.com)
 */

public class RemoteAccount implements Account {
    private final String id;
    private int amount;

    /** Returns account identifier. */

    public RemoteAccount(final String id) {
        this.id = id;
        amount = 0;
    }

    /** Returns account identifier. */

    @Override
    public String getId() {
        return id;
    }

    /** Returns amount of money in the account. */

    @Override
    public synchronized int getAmount() {
        return amount;
    }

    /**
     * Sets amount of money in the account.
     * @param amount amount to set.
     */

    @Override
    public synchronized void setAmount(final int amount) throws Exception {
        if (amount < 0) {
            throw new Exception("Amount can't be less then 0");
        }
        this.amount = amount;
    }
}
