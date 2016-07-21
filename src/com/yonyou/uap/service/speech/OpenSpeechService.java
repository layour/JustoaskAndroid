package com.yonyou.uap.service.speech;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechListener;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SpeechUser;
import com.iflytek.cloud.speech.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.yonyou.uap.um.base.UMEventArgs;
import com.yonyou.uap.um.common.Common;
import com.yonyou.uap.um.core.ActionProcessor;
import com.yonyou.uap.um.core.UMActivity;
import com.yonyou.uap.um.runtime.RTHelper;

/**
 * 讯飞语音服务调用
 * 
 * @author duxfa
 * 
 */
public class OpenSpeechService {

	// 语音合成对文字
	public static void openSpeechBackString(UMEventArgs args) {
		UMActivity ctx = args.getUMActivity();
		String isSystemUiStr = Common.isEmpty(args.getString("isSystemUi")) ? "true" : args.getString("isSystemUi");
		boolean isSystemUi = Boolean.valueOf(isSystemUiStr);
		if(isSystemUi){
			setDialogListener(ctx, args);
		} else {
			setListener(ctx, args);
		}
	}

	// 文字合成对语音
	public static void openStringBackSpeech(UMEventArgs args) {
		UMActivity ctx = args.getUMActivity();
		String text = args.getString("text");
		setParam(ctx, args);
		setVoice(ctx, args, text);
	}

	public static void init(UMEventArgs args) {
		ApplicationInfo appInfo = null;
		UMActivity act = args.getUMActivity();
		try {
			appInfo = act.getPackageManager().getApplicationInfo(act.getPackageName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		String appid = appInfo.metaData.getString("speech_key_anroid");
		SpeechUser.getUser().login(act, null, null, appid,
				new SpeechListener() {

					@Override
					public void onEvent(int eventType, Bundle params) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onData(byte[] buffer) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onCompleted(SpeechError error) {
						// TODO Auto-generated method stub

					}
				});
	}

	public static void setParam(Context context, UMEventArgs args) {
		String voiceName = Common.isEmpty(args.getString("voiceName")) ? "xiaoyan" : args.getString("voiceName");
		String speed = Common.isEmpty(args.getString("speed")) ? "50" : args.getString("speed");
		String volume = Common.isEmpty(args.getString("volume")) ? "50" : args.getString("volume");
		String pitch = Common.isEmpty(args.getString("pitch")) ? "50" : args.getString("pitch");
		
		SpeechSynthesizer speechSynthesizer = SpeechSynthesizer.createSynthesizer(context);
		speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, voiceName);
		speechSynthesizer.setParameter(SpeechConstant.SPEED, speed);
		speechSynthesizer.setParameter(SpeechConstant.VOLUME, volume);
		speechSynthesizer.setParameter(SpeechConstant.PITCH, pitch);
	}

	private static void setVoice(final UMActivity context, final UMEventArgs args, String text) {
		SpeechSynthesizer speechSynthesizer = SpeechSynthesizer.createSynthesizer(context);
		speechSynthesizer.startSpeaking(text, new SynthesizerListener() {

			@Override
			public void onSpeakResumed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSpeakProgress(int progress, int beginPos, int endPos) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSpeakPaused() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSpeakBegin() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onCompleted(SpeechError error) {
				String cb = args.getString(ActionProcessor.CALLBACK, "");
				if (Common.isEmpty(cb)){
					return;
				}
				RTHelper.execCallBack(args);
			}

			@Override
			public void onBufferProgress(int progress, int beginPos, int endPos, String info) {
				// TODO Auto-generated method stub

			}
		});

	}

	public static void setDialogListener(final UMActivity context, final UMEventArgs args) {
		RecognizerDialog recognizerDialog = new RecognizerDialog(context);
		recognizerDialog.setParameter(SpeechConstant.DOMAIN, "iat");
		recognizerDialog.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
		// 显示Dialog
		recognizerDialog.setListener(new RecognizerDialogListener() {
			
			private StringBuffer text = new StringBuffer();

			@Override
			public void onResult(RecognizerResult result, boolean isLast) {
				text.append(JsonParser.parseIatResult(result.getResultString()));
				
				String cb = args.getString(ActionProcessor.CALLBACK, "");
				if(!Common.isEmpty(cb)){
					args.put("text", text);
					RTHelper.execCallBack(args);
				}
			}

			@Override
			public void onError(SpeechError error) {
				// TODO Auto-generated method stub

			}
		});
		recognizerDialog.show();
	}
	
	public static void setListener(final UMActivity context, final UMEventArgs args) {
		RecognizerDialog recognizerDialog = new RecognizerDialog(context);
		recognizerDialog.setParameter(SpeechConstant.DOMAIN, "iat");
		recognizerDialog.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
		// 显示Dialog
		recognizerDialog.setListener(new RecognizerDialogListener() {

			@Override
			public void onResult(RecognizerResult result, boolean isLast) {
				String text = JsonParser.parseIatResult(result.getResultString());
				String cb = args.getString(ActionProcessor.CALLBACK, "");
				args.put("text", text);
				if (Common.isEmpty(cb)){
					return;
				}
				RTHelper.execCallBack(args);
			}

			@Override
			public void onError(SpeechError error) {
				// TODO Auto-generated method stub

			}
		});
		recognizerDialog.show();
	}

}
