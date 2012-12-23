package com.musicg.wave;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.musicg.api.DetectionApi;
import com.musicg.api.WhistleApi;

public class WaveTypeDetector {

	private Wave wave;
	
	private DetectionApi detectionApi ;

	public WaveTypeDetector(Wave wave, DetectionApi detectionApi) {
		this.wave = wave;
		this.detectionApi = detectionApi;
	}

	public double getWhistleProbability() {

		double probability = 0;

		WaveHeader wavHeader = wave.getWaveHeader();

		// fft size 1024, no overlap
		int fftSampleSize = 1024;
		int fftSignalByteLength = fftSampleSize * wavHeader.getBitsPerSample() / 8;
		byte[] audioBytes = wave.getBytes();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(audioBytes);


		// read the byte signals
		try {
			int numFrames = inputStream.available() / fftSignalByteLength;
			byte[] bytes = new byte[fftSignalByteLength];
			int checkLength = 3;
			int passScore = 3;

			ArrayList<Boolean> bufferList = new ArrayList<Boolean>();
			int numWhistles = 0;
			int numPasses = 0;

			// first 10(checkLength) frames
			for (int frameNumber = 0; frameNumber < checkLength; frameNumber++) {
				inputStream.read(bytes);
				boolean isWhistle = detectionApi.isSpecificSound(bytes);
				bufferList.add(isWhistle);
				if (isWhistle) {
					numWhistles++;
				}
				if (numWhistles >= passScore) {
					numPasses++;
				}
				// System.out.println(frameNumber+": "+numWhistles);
			}

			// other frames
			for (int frameNumber = checkLength; frameNumber < numFrames; frameNumber++) {
				inputStream.read(bytes);
				boolean isWhistle = detectionApi.isSpecificSound(bytes);
				if (bufferList.get(0)) {
					numWhistles--;
				}
				bufferList.remove(0);
				bufferList.add(isWhistle);

				if (isWhistle) {
					numWhistles++;
				}
				if (numWhistles >= passScore) {
					numPasses++;
				}
				// System.out.println(frameNumber+": "+numWhistles);
			}
			probability = (double) numPasses / numFrames;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return probability;
	}
}