package evolutionSimulator;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class GenomeTrackControl extends Pane implements Initializable, IObserver{
    @FXML
    private ListView<DataPair<Genome, Integer>> genomeStatListView;
    private SimulationManager simManager;
    private SimulationControl simControl;
    private final List<Animal> selectedGenomeAnimals = new ArrayList<>(3);
    private Genome currentlyTrackerGenome = null;  //used to allow deselecting animals by click on a genome while tracking

    public void setManager(SimulationManager simManager){
        this.simManager = simManager;
    }
    public void setSimControl(SimulationControl simControl){
        this.simControl = simControl;
    }

    public GenomeTrackControl(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/genomeTracker.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException exc) {
            exc.printStackTrace();
            // this is pretty much fatal, so:
            System.exit(1);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        genomeStatListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(DataPair data, boolean empty) {
                super.updateItem(data, empty);
                if (empty || data == null || data.getStringPair() == null) {
                    setText(null);
                } else {
                    setText(data.getStringPair());
                }
            }
        });
        final Tooltip tooltip = new Tooltip();
        tooltip.setText(
                """
                Click on a genome to start tracking all the animals that currently have it,
                those animals will turn blue.
                Click on the same genome to deselect,
                or on another one to change selection.
                """
        );
        genomeStatListView.setTooltip(tooltip);
    }

    @FXML
    private void clearSelectedGenomesPositions(){
        for(Animal animal: selectedGenomeAnimals){
            animal.removeObserver(this);
        }
        selectedGenomeAnimals.clear();
    }

    /**
     *  if is tracking the same genome, stop tracking it
     *  if its tracking another genome, start tracking the chosen one
     *  if it is not tracking, start tracking the chosen one
     */
    @FXML
    private void setSelectedGenomePositions(){
        DataPair<Genome, Integer> chosenDataPair = genomeStatListView.getSelectionModel().getSelectedItem();
        if(chosenDataPair == null)return; //incorrect selection
        Genome chosenGenome = chosenDataPair.getFirst();
        if(chosenGenome.equals(currentlyTrackerGenome)){    //deselect animals
            clearSelectedGenomesPositions();
            currentlyTrackerGenome = null;
        }
        else {  //start tracking another genome
            currentlyTrackerGenome = chosenGenome;
            clearSelectedGenomesPositions();
            for (Animal animal : simManager.getAnimalsByGenome(chosenGenome)) {
                animal.addObserver(this);
                selectedGenomeAnimals.add(animal);
            }
        }
        simControl.showMap();
    }

    /**
     * waits for animal death notifications from the animals who have the currentlyTrackedGenome
     * if the last bearer of the tracked genome has dies, stop tracking
     * @param event expected to be AnimalEvent.DEATH
     * @param parent the animal that dies
     * @param newborn not used
     */
    @Override
    public void notify(AnimalEvent event, Animal parent, Animal newborn) {
        if(event == AnimalEvent.DEATH){
            selectedGenomeAnimals.remove(parent);
            if(selectedGenomeAnimals.size() == 0)currentlyTrackerGenome = null;
        }
    }

    public void updateListView(List<DataPair<Genome, Integer>> dominantGenomesData) {
        genomeStatListView.getItems().clear();
        dominantGenomesData.forEach(dp -> genomeStatListView.getItems().add(dp));
    }

    public List<Animal> getAnimalsWithDominantGenome(){
        return selectedGenomeAnimals;
    }
}
