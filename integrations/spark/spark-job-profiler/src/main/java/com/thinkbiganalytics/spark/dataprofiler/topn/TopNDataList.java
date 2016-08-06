package com.thinkbiganalytics.spark.dataprofiler.topn;

import java.io.Serializable;
import java.util.Iterator;
import java.util.TreeSet;

import com.thinkbiganalytics.spark.dataprofiler.core.ProfilerConfiguration;

/**
 * Class to store top-N items<br>
 * @author jagrut sharma
 *
 */
@SuppressWarnings("serial")
public class TopNDataList implements Serializable {

	private int maxSize = ProfilerConfiguration.NUMBER_OF_TOP_N_VALUES;
	private final TreeSet<TopNDataItem> topNDataItemsForColumn;
	private Long lowestCountSoFar = Long.MAX_VALUE;
	
	
	/**
	 * Constructor to set the number of items in top N list
	 * @param maxSize N in Top N
	 */
	public TopNDataList(int maxSize) {

		if (maxSize > 0) {
			this.maxSize = maxSize;
		}
		topNDataItemsForColumn = new TreeSet<>();
	}


	/**
	 * Add an item for inclusion in top-N list <br>
	 * If two items have same count, the item that was first seen will be kept.
	 * @param newValue value
	 * @param newCount count/frequency
	 */
	public void add(Object newValue, Long newCount) {

		if (topNDataItemsForColumn.size() >= maxSize) {
			if (newCount > lowestCountSoFar) {
				topNDataItemsForColumn.pollFirst();
				addAndUpdateLowestCount(newValue, newCount);
			}
		}
		else {
			addAndUpdateLowestCount(newValue, newCount);
		}
	}

	/**
	 * Helper method <br>
	 * Add a new item in topN structure <br>
	 * Update the lowest count in topN structure
	 * @param newValue value
	 * @param newCount count/frequency
	 */
	private void addAndUpdateLowestCount(Object newValue, Long newCount) {
		topNDataItemsForColumn.add(new TopNDataItem(newValue, newCount));
		lowestCountSoFar = topNDataItemsForColumn.first().getCount();
	}

	
	/**
	 * Print the top-N items as a string. This will give Top-N items in generally expected format (highest count first, lowest count last)<br>
	 * @return String of top-N items with configured delimiters within and between entries (Refer to configuration parameters in ProfilerConfiguration class)
	 */
	public String printTopNItems() {
		int index = 1;
		StringBuilder sb = new StringBuilder();
		Iterator i = topNDataItemsForColumn.descendingIterator();

		while (i.hasNext()) {
			TopNDataItem item = (TopNDataItem) i.next();
			sb.append(index++).append(ProfilerConfiguration.TOP_N_VALUES_INTERNAL_DELIMITER)
					.append(item.getValue())
					.append(ProfilerConfiguration.TOP_N_VALUES_INTERNAL_DELIMITER)
					.append(item.getCount())
					.append(ProfilerConfiguration.TOP_N_VALUES_RECORD_DELIMITER);
		}

		return sb.toString();
	}
	
	
	
	/**
	 * Print all the items ordered from highest count to lowest count <br>
	 * String will have configured delimiters within and between entries (Refer to configuration parameters in ProfilerConfiguration class)
	 */
	@Override
	public String toString() {
		return printTopNItems();
	}


	/**
	 * Get the top-N items as an ordered set (lowest count to highest count)
	 * @return Set with top-N items ordered from lowest count to highest count
	 */
	public TreeSet<TopNDataItem> getTopNDataItemsForColumn() {
		return this.topNDataItemsForColumn;
	}
}
