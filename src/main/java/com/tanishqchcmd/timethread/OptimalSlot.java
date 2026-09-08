package com.tanishqchcmd.timethread;

// We changed 'DayOfWeek day' to 'String day' so it accepts our new exact calendar dates!
public record OptimalSlot(String day, String start, String end) {
}