package info.kgeorgiy.ja.shinkareva.bank;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Client class.
 * @author Shinkareva Alyona (alyona.i.shinkareva@gmail.com)
 */

public final class Client {
    private static final int DEFAULT_REGISTRY_PORT = 1099;
    private static final String HOST_NAME = "//localhost/bank";
    private Client() {}

    /**
     * Main function for imitating work of bank.
     * @param args arguments: <name> <surname> <passport> <accountId> <value to change the amount to>.
     */

    public static void main(final String... args) throws RemoteException {
        final Bank bank;
        try {
            bank = (Bank) LocateRegistry.getRegistry(DEFAULT_REGISTRY_PORT).lookup(HOST_NAME);
        } catch (final NotBoundException e) {
            System.out.println("Bank is not bound");
            return;
        }

        if (Stream.of(args).anyMatch(Objects::isNull)) {
            System.out.println("Invalid arguments." + System.lineSeparator() +
                    "Please enter: <name> <surname> <passport> <accountId> <value to change the amount to>");
        } else {
            String name = args[0];
            String surname = args[1];
            String passport = args[2];
            String subId = args[3];
            int diff = 0;
            try {
                diff = Integer.parseInt(args[4]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid argument: <value to change the amount to> must be a number.");
            }

            Person person = bank.getPersonByPassport(passport, Person.PersonType.REMOTE);
            if (person == null) {
                bank.createPerson(name, surname, passport);
            } else {
                Account account = bank.getAccount(getFullAccountId(passport, subId));
                if (account == null || !bank.getPersonsAccounts(person).contains(account)) {
                    bank.createAccount(subId, person);
                }
            }
            Account account = bank.getAccount(getFullAccountId(passport, subId));
            System.out.println("---------- Person and Account info ----------");
            System.out.println("Person: " + person.getName() + " "+ person.getSurname());
            System.out.println("Account id: " + account.getId());
            try {
                account.setAmount(account.getAmount() + diff);
            } catch (Exception e) {
                System.out.println("The amount can't be less then zero");
            }
            System.out.println("Money: " + account.getAmount());
        }
    }

    private static String getFullAccountId(String passport, String subId) {
        return passport + ":" + subId;
    }
}