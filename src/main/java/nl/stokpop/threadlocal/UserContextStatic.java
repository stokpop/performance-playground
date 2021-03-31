package nl.stokpop.threadlocal;

public class UserContextStatic {

    private static final ThreadLocal<User> userPerThread = ThreadLocal.withInitial(() -> new User("X"));

    public static void setUser(String name) {
        userPerThread.set(new User(name));
    }

    public static User getUser() {
        return userPerThread.get();
    }

}
