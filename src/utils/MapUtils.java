package utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MapUtils {
	
	public synchronized static Map<Integer, SojournTimeData> copyRatings(Map<Integer, SojournTimeData> ratings) {
		Map<Integer, SojournTimeData> tmp = new HashMap<>();
		tmp.putAll(ratings);
		return tmp;
	}
	
	public synchronized static ConcurrentMap<Integer, ConcurrentMap<Integer, Datagram>> copyMap(ConcurrentMap<Integer, ConcurrentMap<Integer, Datagram>> toCopy) {
		ConcurrentMap<Integer, ConcurrentMap<Integer, Datagram>> tmp = new ConcurrentHashMap<>();
		
		for(Integer i : toCopy.keySet()) {
			ConcurrentMap<Integer, Datagram> tmpInner = new ConcurrentHashMap<>();
			tmpInner.putAll(toCopy.get(i));
			tmp.put(i, tmpInner);
		}
		return tmp;
	}
}
