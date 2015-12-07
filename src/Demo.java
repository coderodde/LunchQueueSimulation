import java.util.Random;
import net.coderodde.simulation.lunch.AcademicDegree;
import net.coderodde.simulation.lunch.Cashier;
import net.coderodde.simulation.lunch.Population;
import net.coderodde.simulation.lunch.RandomPopulationGenerator;
import net.coderodde.simulation.lunch.SimulationResult;
import net.coderodde.simulation.lunch.Simulator;

public class Demo {
    
    public static void main(final String... args) {
        long seed = System.nanoTime();
        Random random = new Random(seed);
        
        Population population = 
                RandomPopulationGenerator 
                .withRandom(random)
                .with(15).peopleWithDegree(AcademicDegree.DOCTOR)
                .with(40).peopleWithDegree(AcademicDegree.MASTER)
                .with(100).peopleWithDegree(AcademicDegree.BACHELOR)
                .with(250).peopleWithDegree(AcademicDegree.UNDERGRADUATE)
                .withMeanLunchTime(10800.0)
                .withLunchTimeStandardDeviation(1200.0);
                        
        // Cashier serves in average in 15 seconds, s.d. 2 seconds.
        Cashier cashier = Cashier.withRandom(random)
                                 .withMeanServiceTime(15.0)
                                 .withStandardDeviationOfServiceTime(2.0);
        
        System.out.println("Seed = " + seed);
        
        long startTime = System.nanoTime();
        SimulationResult result = Simulator.simulate()
                                           .withPopulation(population)
                                           .withCashier(cashier);
        long endTime = System.nanoTime();
        
        System.out.printf("Simulated in %.2f milliseconds.\n", 
                          (endTime - startTime) / 1e6);
        
        System.out.println(result);
    }
}