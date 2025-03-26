package nl.stokpop.jmh;

import org.jetbrains.annotations.NotNull;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.*;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
public class LetsCompare {

    public static final Comparator<Person> COMPARE_AGE_LAST_FIRST = Comparator
            .comparingInt(Person::getAge)
            .thenComparing(Person::getLastName)
            .thenComparing(Person::getFirstName);

    List<PersonSameComparator> personsSameComparator = createPersonsSameComparator();
    List<PersonComparatorPerCall> personsComparatorPerCall = createPersonsComparatorPerCall();

    private List<PersonSameComparator> createPersonsSameComparator() {
        List<PersonSameComparator> persons = new ArrayList<>();
        persons.add(new PersonSameComparator("John", "Doe", 30));
        persons.add(new PersonSameComparator("Jane", "Doe", 25));
        persons.add(new PersonSameComparator("John", "Smith", 40));
        persons.add(new PersonSameComparator("Jane", "Smith", 35));
        return persons;
    }

    private List<PersonComparatorPerCall> createPersonsComparatorPerCall() {
        List<PersonComparatorPerCall> persons = new ArrayList<>();
        persons.add(new PersonComparatorPerCall("John", "Doe", 30));
        persons.add(new PersonComparatorPerCall("Jane", "Doe", 25));
        persons.add(new PersonComparatorPerCall("John", "Smith", 40));
        persons.add(new PersonComparatorPerCall("Jane", "Smith", 35));
        return persons;
    }

    @Benchmark
    public void useNewComparatorPerCall(Blackhole blackhole) {
        Collections.sort(personsComparatorPerCall);
        Collections.reverse(personsComparatorPerCall);
        blackhole.consume(personsComparatorPerCall);
    }

    @Benchmark
    public void useSameComparator(Blackhole blackhole) {
        Collections.sort(personsSameComparator);
        Collections.reverse(personsSameComparator);
        blackhole.consume(personsSameComparator);
    }

    @Benchmark
    public void useNewComparatorPerSort(Blackhole blackhole) {
        // should not matter which collection is used, as we use an external comparator
        Collections.sort(personsComparatorPerCall, Comparator.comparingInt(Person::getAge).thenComparing(Person::getLastName).thenComparing(Person::getFirstName));
        Collections.reverse(personsComparatorPerCall);
        blackhole.consume(personsComparatorPerCall);
    }

    @Benchmark
    public void useSameComparatorForSort(Blackhole blackhole) {
        // should not matter which collection is used, as we use an external comparator
        Collections.sort(personsComparatorPerCall, COMPARE_AGE_LAST_FIRST);
        Collections.reverse(personsComparatorPerCall);
        blackhole.consume(personsComparatorPerCall);
    }

    private abstract class Person implements Comparable<Person> {
        private String firstName;
        private String lastName;
        private int age;

        public Person(String firstName, String lastName, int age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public int getAge() {
            return age;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Person person = (Person) obj;
            return age == person.age && firstName.equals(person.firstName) && lastName.equals(person.lastName);
        }

        public int hashCode() {
            return Objects.hash(firstName, lastName, age);
        }
    }

    private class PersonComparatorPerCall extends Person {
        public PersonComparatorPerCall(String firstName, String lastName, int age) {
            super(firstName, lastName, age);
        }

        @Override
        public int compareTo(@NotNull Person o) {
            return Comparator.comparing(Person::getFirstName)
                    .thenComparing(Person::getLastName)
                    .thenComparingInt(Person::getAge)
                    .compare(this, o);
        }
    }

    private class PersonSameComparator extends Person {
        private static final Comparator<Person> PERSON_COMPARATOR = Comparator.comparing(Person::getFirstName)
                .thenComparing(Person::getLastName)
                .thenComparingInt(Person::getAge);

        public PersonSameComparator(String firstName, String lastName, int age) {
            super(firstName, lastName, age);
        }

        @Override
        public int compareTo(@NotNull Person o) {
            return PERSON_COMPARATOR.compare(this, o);
        }
    }

    public static void main(String[] args) throws Exception {
        final Options options = new OptionsBuilder()
                .forks(1)
                .threads(1)
                .warmupIterations(2)
                .measurementIterations(4)
                .verbosity(VerboseMode.EXTRA)
                .include("LetsCompare.use")
                .build();

        new Runner(options).run();
    }
}
