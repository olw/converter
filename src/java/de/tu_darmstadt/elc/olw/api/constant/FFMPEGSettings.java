
package de.tu_darmstadt.elc.olw.api.constant;


public class FFMPEGSettings {
	public final static String[] RESOLUTION_SET = { "480x360", "848x480",
			"1280x720", "1920x1080", "320x240" };

	public static final String MP4_360P = "-ss 00:00:00   -s 480x360 -aspect 4:3 -acodec libfaac -ab 96k -vcodec libx264 -coder 1 -flags +loop -cmp +chroma -partitions -parti8x8-parti4x4-partp8x8-partb8x8 -me_method dia -subq 2 -me_range 16 -g 250 -keyint_min 25 -sc_threshold 40 -i_qfactor 0.71 -b_strategy 1 -qcomp 0.6 -qmin 10 -qmax 51 -qdiff 4 -bf 3 -refs 1 -direct-pred 1 -trellis 0 -flags +mv4+aic -wpredp 2 -crf 25 -threads 0 -f mp4";
	public static final String MP4_480P = "-ss 00:00:00   -s 848x480 -aspect 16:9 -acodec libfaac -ab 96k -vcodec libx264 -coder 1 -flags +loop -cmp +chroma -partitions -parti8x8-parti4x4-partp8x8-partb8x8 -me_method dia -subq 2 -me_range 16 -g 250 -keyint_min 25 -sc_threshold 40 -i_qfactor 0.71 -b_strategy 1 -qcomp 0.6 -qmin 10 -qmax 51 -qdiff 4 -bf 3 -refs 1 -direct-pred 1 -trellis 0 -flags +mv4+aic -wpredp 2 -crf 25 -threads 0 -f mp4";
	public static final String MP4_720P = "-ss 00:00:00   -s 1280x720 -aspect 16:9 -acodec libfaac -ab 128k -vcodec libx264 -coder 1 -flags +loop -cmp +chroma -partitions -parti8x8-parti4x4-partp8x8-partb8x8 -me_method dia -subq 2 -me_range 16 -g 250 -keyint_min 25 -sc_threshold 40 -i_qfactor 0.71 -b_strategy 1 -qcomp 0.6 -qmin 10 -qmax 51 -qdiff 4 -bf 3 -refs 1 -direct-pred 1 -trellis 0 -flags +mv4+aic -wpredp 2 -crf 22 -threads 0 -f mp4";
	public static final String MP4_1080P = "-ss 00:00:00   -s 1920x1080 -aspect 16:9 -acodec libfaac -ab 128k -vcodec libx264 -coder 1 -flags +loop -cmp +chroma -partitions -parti8x8-parti4x4-partp8x8-partb8x8 -me_method dia -subq 2 -me_range 16 -g 250 -keyint_min 25 -sc_threshold 40 -i_qfactor 0.71 -b_strategy 1 -qcomp 0.6 -qmin 10 -qmax 51 -qdiff 4 -bf 3 -refs 1 -direct-pred 1 -trellis 0 -flags +mv4+aic -wpredp 2 -crf 22 -threads 0 -f mp4";

	public static final String MP4_MOBILE = "-ss 00:00:00   -s 320x240 -aspect 4:3 -acodec libfaac -ab 96k -vcodec libx264 -coder 1 -flags +loop -cmp +chroma -partitions -parti8x8-parti4x4-partp8x8-partb8x8 -me_method dia -subq 2 -me_range 16 -g 250 -keyint_min 25 -sc_threshold 40 -i_qfactor 0.71 -b_strategy 1 -qcomp 0.6 -qmin 10 -qmax 51 -qdiff 4 -bf 3 -refs 1 -direct-pred 1 -trellis 0 -flags +mv4+aic -wpredp 2 -crf 25 -threads 0 -f mp4";
	public static final String FLV_CAM = "-ss 00:00:00   -s 584x438 -aspect 4:3 -acodec libfaac -ab 96k -vcodec libx264 -coder 1 -flags +loop -cmp +chroma -partitions -parti8x8-parti4x4-partp8x8-partb8x8 -me_method dia -subq 2 -me_range 16 -g 250 -keyint_min 25 -sc_threshold 40 -i_qfactor 0.71 -b_strategy 1 -qcomp 0.6 -qmin 10 -qmax 51 -qdiff 4 -bf 3 -refs 1 -direct-pred 1 -trellis 0 -flags +mv4+aic -wpredp 2 -crf 25 -threads 0 -f flv";
	public static final String FLV_360P = "-ss 00:00:00   -s 480x360 -aspect 4:3 -acodec libfaac -ab 96k -vcodec libx264 -coder 1 -flags +loop -cmp +chroma -partitions -parti8x8-parti4x4-partp8x8-partb8x8 -me_method dia -subq 2 -me_range 16 -g 250 -keyint_min 25 -sc_threshold 40 -i_qfactor 0.71 -b_strategy 1 -qcomp 0.6 -qmin 10 -qmax 51 -qdiff 4 -bf 3 -refs 1 -direct-pred 1 -trellis 0 -flags +mv4+aic -wpredp 2 -crf 25 -threads 0 -f flv";
	public static final String FLV_360P_MUTE ="-ss 00:00:00   -s 480x360 -aspect 4:3 -an -vcodec libx264 -coder 1 -flags +loop -cmp +chroma -partitions -parti8x8-parti4x4-partp8x8-partb8x8 -me_method dia -subq 2 -me_range 16 -g 250 -keyint_min 25 -sc_threshold 40 -i_qfactor 0.71 -b_strategy 1 -qcomp 0.6 -qmin 10 -qmax 51 -qdiff 4 -bf 3 -refs 1 -direct-pred 1 -trellis 0 -flags +mv4+aic -wpredp 2 -crf 25 -threads 0 -f flv";
	public static final String FLV_DUMMY = "-ss 00:00:00   -s 302x140  -acodec libfaac -ab 96k -vcodec libx264 -coder 1 -flags +loop -cmp +chroma -partitions -parti8x8-parti4x4-partp8x8-partb8x8 -me_method dia -subq 2 -me_range 16 -g 250 -keyint_min 25 -sc_threshold 40 -i_qfactor 0.71 -b_strategy 1 -qcomp 0.6 -qmin 10 -qmax 51 -qdiff 4 -bf 3 -refs 1 -direct-pred 1 -trellis 0 -flags +mv4+aic -wpredp 2 -crf 25 -threads 0 -f flv"; 
	public static final String FLV_480P = "-ss 00:00:00   -s 848x480 -aspect 16:9 -acodec libfaac -ab 96k -vcodec libx264 -coder 1 -flags +loop -cmp +chroma -partitions -parti8x8-parti4x4-partp8x8-partb8x8 -me_method dia -subq 2 -me_range 16 -g 250 -keyint_min 25 -sc_threshold 40 -i_qfactor 0.71 -b_strategy 1 -qcomp 0.6 -qmin 10 -qmax 51 -qdiff 4 -bf 3 -refs 1 -direct-pred 1 -trellis 0 -flags +mv4+aic -wpredp 2 -crf 25 -threads 0 -f flv";
	
	public static final String FLV_AUDIO = "-ss 00:00:00  -acodec libmp3lame -ab 96k -ar 44100 -ac 1 -f flv";
	
	public static final String MP3_128K = "-ss 00:00:00   -acodec libmp3lame -ab 128k -map a -f mp3";
	public static final String AAC_128K = "-ss 00:00:00   -acodec libfaac -ab 128k";
	public static final String OGG_128K = "-ss 00:00:00  -acodec libvorbis -aq 5 -map a -f ogg";
	
	public static final String WEBM_360P = "-s 480x360 -aspect 4:3 -vcodec libvpx -acodec libvorbis -ab 96k -crf 25 -f webm";
	public static final String WEBM_480P = "-s 848x480 -aspect 16:9 -vcodec libvpx -acodec libvorbis -ab 96k -crf 25 -f webm";
}
