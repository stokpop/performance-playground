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

    List<PersonPerCallCompare> personsPerCall = createPersonsPerCallList();
    List<PersonPerObjectCompare> personsPerObject = createPersonsPerObjectList();

    private List<PersonPerCallCompare> createPersonsPerCallList() {
        List<PersonPerCallCompare> persons = new ArrayList<>();
        persons.add(new PersonPerCallCompare("John", "Doe", 30));
        persons.add(new PersonPerCallCompare("Jane", "Doe", 25));
        persons.add(new PersonPerCallCompare("John", "Smith", 40));
        persons.add(new PersonPerCallCompare("Jane", "Smith", 35));
        return persons;
    }

    private List<PersonPerObjectCompare> createPersonsPerObjectList() {
        List<PersonPerObjectCompare> persons = new ArrayList<>();
        persons.add(new PersonPerObjectCompare("John", "Doe", 30));
        persons.add(new PersonPerObjectCompare("Jane", "Doe", 25));
        persons.add(new PersonPerObjectCompare("John", "Smith", 40));
        persons.add(new PersonPerObjectCompare("Jane", "Smith", 35));
        return persons;
    }

    @Benchmark
    public void usePerObjectCompare(Blackhole blackhole) {
        Collections.sort(personsPerObject);
        Collections.reverse(personsPerObject);
        blackhole.consume(personsPerObject);
    }

    @Benchmark
    public void usePerCallCompare(Blackhole blackhole) {
        Collections.sort(personsPerCall);
        Collections.reverse(personsPerCall);
        blackhole.consume(personsPerCall);
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

    private class PersonPerObjectCompare extends Person {
        public PersonPerObjectCompare(String firstName, String lastName, int age) {
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

    private class PersonPerCallCompare extends Person {
        private Comparator<Person> personComparator = Comparator.comparing(Person::getFirstName)
                .thenComparing(Person::getLastName)
                .thenComparingInt(Person::getAge);

        public PersonPerCallCompare(String firstName, String lastName, int age) {
            super(firstName, lastName, age);
        }

        @Override
        public int compareTo(@NotNull Person o) {
            return personComparator.compare(this, o);
        }
    }

    public static void main(String[] args) throws Exception {
        final Options options = new OptionsBuilder()
                .forks(1)
                .threads(1)
                .warmupIterations(2)
                .measurementIterations(4)
                .verbosity(VerboseMode.EXTRA)
                .include("usePer")
                .build();

        new Runner(options).run();
    }
}
