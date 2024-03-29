/*
 * Copyright (C) 2012 Jacquet Wong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.musicg.main.demo;

import com.musicg.api.DetectionApi;
import com.musicg.api.WhistleApi;
import com.musicg.wave.Wave;
import com.musicg.wave.WaveTypeDetector;

public class WhistleApiDemo {
	public static void main(String[] args) {
		String filename = "audio_work/whistle.wav";

		// create a wave object
		Wave wave = new Wave(filename);
		DetectionApi whistleDetectionApi = new WhistleApi(wave.getWaveHeader());

		WaveTypeDetector waveTypeDetector = new WaveTypeDetector(wave, whistleDetectionApi);
		System.out.println("Is whistle probability: " + waveTypeDetector.getWhistleProbability());
	}
}