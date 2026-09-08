package com.tanishqchcmd.timethread;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean recurring;

    // Used when recurring = false
    private LocalDate specificDate;

    // Used when recurring = true: comma-separated day numbers, 0=Sunday..6=Saturday
    // (matches FullCalendar's own convention directly), e.g. "1,3,5" for Mon/Wed/Fri
    private String daysOfWeek;

    // Optional bounds on a recurring task — null means unbounded on that side
    private LocalDate recurStart;
    private LocalDate recurEnd;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Student owner;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public boolean isRecurring() { return recurring; }
    public void setRecurring(boolean recurring) { this.recurring = recurring; }
    public LocalDate getSpecificDate() { return specificDate; }
    public void setSpecificDate(LocalDate specificDate) { this.specificDate = specificDate; }
    public String getDaysOfWeek() { return daysOfWeek; }
    public void setDaysOfWeek(String daysOfWeek) { this.daysOfWeek = daysOfWeek; }
    public LocalDate getRecurStart() { return recurStart; }
    public void setRecurStart(LocalDate recurStart) { this.recurStart = recurStart; }
    public LocalDate getRecurEnd() { return recurEnd; }
    public void setRecurEnd(LocalDate recurEnd) { this.recurEnd = recurEnd; }
    public Student getOwner() { return owner; }
    public void setOwner(Student owner) { this.owner = owner; }
}