package betterfoliage.render.generator;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ParameterList {
	private final Map<String,String> params;
	private final String value;
	
	public ParameterList(Map<String,String> params, @Nullable String value) {
		this.params = params;
		this.value = value;
	}
	
	public String get(String key) {
		return this.params.get(key);
	}
	
	public boolean contains(String key) {
		return this.params.containsKey(key);
	}
	
	@Nullable
	public String getValue() {
		return this.value;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		this.params.entrySet().stream()
				   .sorted(Map.Entry.comparingByKey())
				   .forEach(e -> builder.append('|')
										.append(e.getKey())
										.append('=')
										.append(e.getValue()));
		if(this.value != null) builder.append('|').append(this.value);
		return builder.toString();
	}
	
	public static ParameterList fromString(String input) {
		HashMap<String,String> params = new HashMap<>();
		String value = null;
		int start = input.indexOf('|');
		input = start < 0 ? "" : input.substring(start);
		String[] slices = input.split("\\|");
		for(String slice : slices) {
			if(slice.contains("=")) {
				String[] keyValue = slice.split("=");
				if(keyValue.length == 2) {
					params.put(keyValue[0], keyValue[1]);
				}
			}
			else value = slice;
		}
		return new ParameterList(params, value);
	}
}