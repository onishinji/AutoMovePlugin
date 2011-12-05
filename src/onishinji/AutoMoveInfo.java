package onishinji;

import java.text.DecimalFormat;

public class AutoMoveInfo {


    int nbTotalSteps;
    int currentStep;
    
    public AutoMoveInfo(int nbTotalSteps, int currentStep) { 
        this.nbTotalSteps = nbTotalSteps;
        this.currentStep = currentStep;
    }
    
    public AutoMoveInfo() { 
        this.nbTotalSteps = 1;
        this.currentStep = 1;
    }

    public int getNbTotalSteps() {
        return nbTotalSteps;
    }

    public void setNbTotalSteps(int nbTotalSteps) {
        this.nbTotalSteps = nbTotalSteps;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }
    
    String getCurrentPourcent()
    { 
        
        double a = this.currentStep;
        double b = this.nbTotalSteps;  
        double resultat = a / b; 
        double resultatFinal = resultat * 100; 
 
        DecimalFormat df = new DecimalFormat("###.##");
        return df.format(resultatFinal) + " %";   
    }
}
