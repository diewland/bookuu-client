package com.diewland.comic.hunter;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// String resp_str = "[ \"Nokia X\", \"Nokia X+\", \"Nokia XL\" ]";
		String resp_str = "[ \"Nokia X\" ]";
		String data[] = resp_str.replace("[ ", "")
                                .replace(" ]", "")
                                .split(", ");

		for (String name : data) {
			System.out.println(name);
		}
	}

}
