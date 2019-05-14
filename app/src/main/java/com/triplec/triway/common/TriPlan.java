package com.triplec.triway.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TriPlan implements Serializable{
    private List<TriPlace> list;
    //private TriPlace triPlace;
    private String name;
    private String planId;
    private TriPlan() {
        list = new ArrayList<>();
    }
    private String dateModified;
    private TriPlan(TriPlanBuilder builder){
        //name = p;
        planId = "";
        list = builder.list;
    }

    public String getDateModified() {
        return dateModified;
    }
    public void setDate(String newDate) {
        dateModified = newDate;
    }

    public List<TriPlace> getPlaceList() {
        return this.list;
    }
    public void setList(List<TriPlace> newList) {
        list = newList;
    }
    private List<TriPlace> getTopSeven(List<TriPlace> allPlaces){
        if (allPlaces.size() < 7)
            return allPlaces;
        List<TriPlace> list = allPlaces;


        return list.subList(0,7);
    }

    public String getName() {
        return name;
    }

    public void setName(String plan_name) {
        if (plan_name.length() == 0)
            return;
        name = plan_name;
        return;
    }
    public void setId(String id) {
        planId = id;
    }
    public String getId() {
        return planId;
    }

    public static class TriPlanBuilder {
        private List<TriPlace> list;
        public TriPlanBuilder() {
            list = new ArrayList<>();
        }
        public TriPlanBuilder addPlace(TriPlace p){
            list.add(p);
            return this;
        }
        public TriPlanBuilder removePlace(TriPlace p){
            list.remove(p);
            return this;
        }
        public TriPlanBuilder addPlaceList(List<TriPlace> newList) {
            list=newList;
            return this;
        }
        public TriPlan buildPlan() {
            return new TriPlan(this);
        }
    }
}
