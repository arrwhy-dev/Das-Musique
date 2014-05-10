package models;

public class Track {
	public String key;
	public String trackName;
	public String artistName;
	public String albumName;
	public String albumArt;

	public Track(String k, String name, String artist, String album, String uri) {
		key = k;
		trackName = name;
		artistName = artist;
		albumName = album;
		albumArt = uri;
	}
}