package cz.momento.controllers;

import javafx.scene.control.ComboBox;

public class ControllerTask {
    public ComboBox employee;
    public ComboBox priority;

    public void ControllerTask(){

        setPriorityOptions();
    }

    public void setPriorityOptions(){
        for(int i = 1; i <= 5;i++){
            priority.getItems().add(i);
        }
    }

    public void setEmployeeOptions(){
        /*
        Tady by to chtělo nějak získat všechny zaměstnance a pak je přidat do komboboxu

        String[] employees = getEmployees();
        for(String employee : employees){
            employee.getItems().add(employee);
        }
         */
    }
}
