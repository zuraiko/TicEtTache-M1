package com.abgames.tictache.Model;

/**
 * Class filter that can allow to display what we want
 */
public class Filter {

    /**
     * Status completed
     */
    private boolean completed;
    /**
     * Status continued
     */
    private boolean continued;
    /**
     * Status expired
     */
    private boolean expired;

    public Filter() {
        resetFilter();
    }

    public void resetFilter(){
        completed = false;
        continued = false;
        expired = false;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isContinued() {
        return continued;
    }

    public void setContinued(boolean continued) {
        this.continued = continued;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
