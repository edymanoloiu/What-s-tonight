package com.example.Edi.myapplication.backend;

import com.googlecode.objectify.annotation.Entity;

import java.util.LinkedList;


@Entity
public class Event {
    @com.googlecode.objectify.annotation.Id
    private Long Id;
    private String name;
    private String description;
    private String creationDate;
    private String eventTime;
    private String ownerName;
    private Category category;
    private LocationInfo location;
    private int maximumPeopleCount;
    private int participantsNo;
    private LinkedList<String> participantsList;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public LocationInfo getLocation() {
        return location;
    }

    public void setLocation(LocationInfo location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public int getMaximumPeopleCount() {
        return maximumPeopleCount;
    }

    public void setMaximumPeopleCount(int maximumPeopleCount) {
        this.maximumPeopleCount = maximumPeopleCount;
    }

    public int getParticipantsNo() {
        return participantsNo;
    }

    public void setParticipantsNo(int participantsNo) {
        this.participantsNo = participantsNo;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public LinkedList<String> getParticipantsList() {
        return participantsList;
    }

    public void setParticipantsList(LinkedList<String> participantsList) {
        this.participantsList = participantsList;
    }
}
