package info.kgeorgiy.ja.shinkareva.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * {@link Remote} interface for implementing {@link RemoteBank}.
 * @author Shinkareva Alyona (alyona.i.shinkareva@gmail.com)
 */
public interface Bank extends Remote {
    /**
     * Creates a new account with specified identifier if it does not already exist.
     * @param id account id
     * @return created or existing account.
     */
    Account createAccount(String id, Person person) throws RemoteException;

    /**
     * Returns account by identifier.
     * @param id account id
     * @return account with specified identifier or {@code null} if such account does not exist.
     */
    Account getAccount(String id) throws RemoteException;

    /**
     * Creates a new person with specified name, surname, passport if it does not already exist.
     * @param name name
     * @param surname surname
     * @param passport passport
     * @return created or existing person.
     */

    Person createPerson(String name, String surname, String passport) throws RemoteException;

    /**
     * Returns person by passport.
     * @param passport passport
     * @param personType type od person: local or remote.
     * @return person with specified passport or {@code null} if such person does not exist.
     */
    Person getPersonByPassport(String passport, Person.PersonType personType) throws RemoteException;

    /**
     * Return all person's accounts.
     * @param person person
     * @return set of all person's accounts
     */
    Set<Account> getPersonsAccounts(Person person) throws RemoteException;
}
