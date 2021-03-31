package nl.stokpop.threadlocal;

public class UserContext {

    private final String contextName;

    private final ThreadLocal<User> userPerThreadPerInstance =
            ThreadLocal.withInitial(() -> new User("X"));

    public UserContext(String contextName) {
        this.contextName = contextName;
    }

    public void setUser(String name) {
        userPerThreadPerInstance.set(new User(name));
    }

    public User getUser() {
        return userPerThreadPerInstance.get();
    }

    @Override
    public String toString() {
        return contextName;
    }
}
