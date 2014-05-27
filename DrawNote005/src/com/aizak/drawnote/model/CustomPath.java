package com.aizak.drawnote.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Path;

import com.aizak.drawnote.model.CustomPath.PathAction.PathActionType;

public class CustomPath extends Path implements Serializable {

	private static final long serialVersionUID = -5974912367682897467L;

	public ArrayList<PathAction> actions = new ArrayList<CustomPath.PathAction>();

	public CustomPath() {
		super();
	}

	public CustomPath(CustomPath path) {
		super(path);
		actions = new ArrayList<CustomPath.PathAction>(path.actions);
		drawThisPath();
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		drawThisPath();
	}

	@Override
	public void moveTo(float x, float y) {
		actions.add(new ActionMove(x, y));
		super.moveTo(x, y);
	}

	@Override
	public void lineTo(float x, float y) {
		actions.add(new ActionLine(x, y));
		super.lineTo(x, y);
	}

	public void drawThisPath() {
		for (PathAction p : actions) {
			if (p.getType().equals(PathAction.PathActionType.MOVE_TO)) {
				super.moveTo(p.getX(), p.getY());
			} else if (p.getType().equals(PathActionType.LINE_TO)) {
				super.lineTo(p.getX(), p.getY());
			}
		}
	}

	public void moveCoordinates(float moveX, float moveY) {
		for (PathAction p : actions) {
			p.setX((p.getX()+moveX));
			p.setY((p.getY()+moveY));

			if (p.getType().equals(PathAction.PathActionType.MOVE_TO)) {
				super.moveTo(p.getX(), p.getY());
			} else if (p.getType().equals(PathActionType.LINE_TO)) {
				super.lineTo(p.getX(), p.getY());
			}
		}
	}

	public interface PathAction {
		public enum PathActionType {
			LINE_TO, MOVE_TO
		};

		public PathActionType getType();

		public float getX();

		public float getY();

		public void setX(float x);

		public void setY(float y);
	}

	public class ActionMove implements PathAction, Serializable {
		private static final long serialVersionUID = -7198142191254133295L;

		private float x;

		private float y;

		public ActionMove(float x, float y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public PathActionType getType() {
			return PathActionType.MOVE_TO;
		}

		@Override
		public float getX() {
			return x;
		}

		@Override
		public float getY() {
			return y;
		}

		@Override
		public void setX(float x) {
			this.x = x;
		}

		@Override
		public void setY(float y) {
			this.y = y;
		}
	}

	public class ActionLine implements PathAction, Serializable {
		private static final long serialVersionUID = 8307137961494172589L;

		private float x;

		private float y;

		public ActionLine(float x, float y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public PathActionType getType() {
			return PathActionType.LINE_TO;
		}

		@Override
		public float getX() {
			return x;
		}

		@Override
		public float getY() {
			return y;
		}

		@Override
		public void setX(float x) {
			this.x = x;
		}

		@Override
		public void setY(float y) {
			this.y = y;
		}

	}
}
