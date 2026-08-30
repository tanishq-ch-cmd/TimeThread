package com.tanishqchcmd.timethread;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DevDataSeeder {

    @Bean
    public CommandLineRunner seedDemoGroups(GroupRepository groupRepository) {
        return args -> {
            if (groupRepository.count() == 0) {
                Group physics = new Group();
                physics.setName("Physics Study");
                Group jee = new Group();
                jee.setName("JEE Prep");
                Group project = new Group();
                project.setName("Project Team");
                groupRepository.save(physics);
                groupRepository.save(jee);
                groupRepository.save(project);
            }
        };
    }
}