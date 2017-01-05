package ar.edu.utn.ba.dds.commons;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//Se utiliza Principalmente en Estadisticas. Existe simplemente porque debieron cambiar el Metodo "sortMapByValue"

public class MapsCommons {

	public static<K, V extends Comparable<V>> Map<K, V> sortMapByValue(Map<K, V> map) {
		
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o2.getValue().compareTo(o1.getValue()));
			}
			
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Iterator<Map.Entry<K, V>> it = list.iterator(); it.hasNext();) {
			Map.Entry<K, V> entry = it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

}
