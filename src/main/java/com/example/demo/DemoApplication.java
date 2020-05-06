package com.example.demo;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    public static interface IExecutor {
        int execute(String command);
    }

    @Bean
    public static IExecutor cliExecutorFactory() {
        return new CliExecutor();
    }

    public static class CliExecutor implements IExecutor {
        @Override
        public int execute(String command) {
            try {
                System.out.println("running command \"" + command + "\"");

                // String[] args = new String[] { "sh", "-c", command };
                int ret = Runtime.getRuntime().exec(command).waitFor();     // sink
                
                System.out.println("done running command (ret=" + ret + ")");
                return ret;

            } catch (IOException|InterruptedException e) {
                e.printStackTrace();
                return -1;   
            }
        }
    }

    /*- - - - - 1st-party logic - - - - -*/
    @Autowired
    private IExecutor injectedExecutor;
 
    @Override
    public void run(String... args) {
		String cmd = System.getenv("SCRIPT");    // unsafe source
        if (cmd == null) {
			return;
		}

		injectedExecutor.execute(cmd);       // unsatized flow
		
		IExecutor directlyUsedExecutor = new CliExecutor();
		directlyUsedExecutor.execute(cmd);       // unsatized flow

		try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) { e.printStackTrace(); }
    }
 
    /*- - - - - Spring boilerplate - - - - -*/
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
