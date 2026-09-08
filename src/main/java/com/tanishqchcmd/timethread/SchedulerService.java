package com.tanishqchcmd.timethread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class SchedulerService {

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private AvailabilityRepository availabilityRepository;

    public List<OptimalSlot> findOptimalSlots(Long groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow();
        List<Student> members = group.getStudents();
        int groupSize = members.size();
        List<OptimalSlot> results = new ArrayList<>();

        if (groupSize == 0) return results;

        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate targetDate = today.plusDays(i);
            List<Availability> dayBlocks = new ArrayList<>();

            for (Student member : members) {
                // FIX 1: The garbled typo is fixed!
                dayBlocks.addAll(availabilityRepository.findByStudentAndDate(member, targetDate));
            }

            results.addAll(sweepDay(dayBlocks, targetDate, groupSize));
        }
        return results;
    }

    private List<OptimalSlot> sweepDay(List<Availability> blocks, LocalDate date, int groupSize) {
        List<OptimalSlot> slots = new ArrayList<>();
        if (blocks.isEmpty()) return slots;

        List<Event> events = new ArrayList<>();
        for (Availability b : blocks) {
            events.add(new Event(b.getStartTime(), 1));
            events.add(new Event(b.getEndTime(), -1));
        }

        events.sort(Comparator.comparing(Event::getTime).thenComparing(Event::getType));

        int concurrentFree = 0;
        LocalTime overlapStart = null;

        String dayString = date.getDayOfWeek().toString() + " (" + date.toString() + ")";

        for (Event event : events) {
            concurrentFree += event.getType();

            if (concurrentFree == groupSize && overlapStart == null) {
                overlapStart = event.getTime();
            }
            else if (concurrentFree < groupSize && overlapStart != null) {
                // FIX 2: Since OptimalSlot is a record, we pass the data directly into the constructor!
                slots.add(new OptimalSlot(dayString, overlapStart.toString(), event.getTime().toString()));

                overlapStart = null;
            }
        }
        return slots;
    }

    private static class Event {
        private LocalTime time;
        private int type;

        public Event(LocalTime time, int type) {
            this.time = time;
            this.type = type;
        }
        public LocalTime getTime() { return time; }
        public int getType() { return type; }
    }
}