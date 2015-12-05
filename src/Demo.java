import java.util.Random;
import net.coderodde.simulation.lunch.AcademicDegree;
import net.coderodde.simulation.lunch.Cashier;
import net.coderodde.simulation.lunch.Population;
import net.coderodde.simulation.lunch.RandomPopulationGenerator;
import net.coderodde.simulation.lunch.SimulationResult;
import net.coderodde.simulation.lunch.Simulator;

public class Demo {
public static void main(final String... args) {
        Cashier cashier;
        Population population;
        
//        Person person1 = new Person("Al", "Funky", AcademicDegree.UNDERGRADUATE);
//        Person person2 = new Person("El", "Funky", AcademicDegree.UNDERGRADUATE);
//        Person person3 = new Person("Ol", "Funky", AcademicDegree.UNDERGRADUATE);
//        cashier = new Cashier(12.0, 0.0, new Random(1));
//        population = new Population();
//        
//        population.addPerson(person1, 0.0);
//        population.addPerson(person2, 0.0);
//        population.addPerson(person3, 0.0);
//        
//        SimulationResult result1 =  new Simulator().simulate(population, cashier);
//        String s = result1.toString();
//        System.out.println(s);
//        
//        System.exit(0);
        
        long seed = System.nanoTime();
        Random random = new Random(seed);
        
        population = 
                RandomPopulationGenerator
                        .withRandom(random)
                        .withDegreeCount(AcademicDegree.DOCTOR,        15)
                        .withDegreeCount(AcademicDegree.MASTER,        40)
                        .withDegreeCount(AcademicDegree.BACHELOR,      100)
                        .withDegreeCount(AcademicDegree.UNDERGRADUATE, 250)
                        .withMeanLunchTime(10800.0)
                        .withLunchTimeStandardDeviation(2000.0);
                        
        // Cashier serves in average in 15 seconds, s.d. 2 seconds.
        cashier = Cashier.withRandom(random)
                         .withMeanServiceTime(15.0)
                         .withStandardDeviationOfServiceTime(2.0);
        
        System.out.println("Seed = " + seed);
        
        long startTime = System.nanoTime();
        SimulationResult result = new Simulator().simulate(population, cashier);
        long endTime = System.nanoTime();
        
        System.out.printf("Simulated in %.2f milliseconds.\n", 
                          (endTime - startTime) / 1e6);
        
        System.out.println(result);
    }
}