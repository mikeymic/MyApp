package com.aizak.drawnote.activity.database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.aizak.drawnote.model.Line;

public class SerializeManager {

	public static byte[] serializeData(Bitmap data) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		data.compress(CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}

	public static byte[] serializeData(Object data) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			ObjectOutputStream out = new ObjectOutputStream(stream);
			out.writeObject(data);
			out.close();
		} catch (IOException e) {
		} catch (NullPointerException e) {
		}
		return stream.toByteArray();
	}

	public static byte[] serializeData(ArrayList<Line> data) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			ObjectOutputStream out = new ObjectOutputStream(stream);
			out.writeObject(data);
			out.close();
		} catch (IOException e) {
		} catch (NullPointerException e) {
		}
		return stream.toByteArray();
	}

	public static Object deserializeData(byte[] stream) {
		Object data = null;
		try {
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(stream));
			data = in.readObject();
			in.close();
		} catch (ClassNotFoundException e) {
		} catch (StreamCorruptedException e) {
		} catch (IOException e) {
		} catch (NullPointerException e) {
		}
		return data;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Line> deserializeData(byte[] stream, int i) {
		ArrayList<Line> data = null;
		try {
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(stream));
			data = (ArrayList<Line>) in.readObject();
			in.close();
		} catch (ClassNotFoundException e) {
		} catch (StreamCorruptedException e) {
		} catch (IOException e) {
		} catch (NullPointerException e) {
		}
		return data;
	}
}
