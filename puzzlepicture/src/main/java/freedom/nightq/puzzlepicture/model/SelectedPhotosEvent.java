package freedom.nightq.puzzlepicture.model;

import java.util.LinkedHashMap;

/**
 */
public class SelectedPhotosEvent {
    public LinkedHashMap<String, String> selectedBeans;

    public SelectedPhotosEvent(LinkedHashMap<String, String> selectedBeans) {
        this.selectedBeans = selectedBeans;
    }
}
