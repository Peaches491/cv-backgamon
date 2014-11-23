package components.base;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class ComponentManager {
	
	private LinkedList<Component> components = new LinkedList<Component>();
	private HashMap<String, Object> dataRegistry = new HashMap<String, Object>();
	
	public ComponentManager() {
		
	}
	
	public void addComponent(Component comp) {
		components.add(comp);
		comp.setManager(this);
	}

	public void initialize() {
		components.getLast().setApply(true);
		components.getLast().setVisualize(true);
	}

	public Collection<Component> getComponents() {
		return components;
	}

	public void setRegistryData(String dataKey, Object obj) {
		dataRegistry.put(dataKey, obj);
	}
	
	public Object getRegistryData(String dataKey) {
		return dataRegistry.get(dataKey);
	}
	
}
