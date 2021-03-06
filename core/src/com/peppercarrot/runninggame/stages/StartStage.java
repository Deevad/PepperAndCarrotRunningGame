package com.peppercarrot.runninggame.stages;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.peppercarrot.runninggame.PaCGame;
import com.peppercarrot.runninggame.screens.WorldScreen;
import com.peppercarrot.runninggame.utils.Assets;
import com.peppercarrot.runninggame.utils.Constants;

public class StartStage extends AbstractStage {
	Table rootTable;
	boolean goToWorldMap;

	public StartStage() {
		this.rootTable = new Table(Assets.I.skin);
		rootTable.setFillParent(true);
		rootTable.setWidth(Constants.VIRTUAL_WIDTH);
		rootTable.setHeight(Constants.VIRTUAL_HEIGHT);
		rootTable.setBackground("button-down"); // invisible button
		rootTable.setTouchable(Touchable.enabled);
		rootTable.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				goToWorldMap = true;
				event.cancel();
				return true;
			}
		});
		final Label label = new Label("Touch to start", Assets.I.skin, "default");
		label.addAction(Actions.forever(Actions.sequence(Actions.fadeOut(1f), Actions.fadeIn(1.4f))));
		rootTable.add(label).bottom().padBottom(60);
		rootTable.bottom();
		this.addActor(rootTable);
	}

	/**
	 * 
	 * @param delta
	 */
	public void render(float delta) {
		this.act(delta);
		this.draw();
		if (goToWorldMap) {
			switchScreen(0.25f);
		}
	}

	/**
	 * Fade out animation that takes fadeOutTime long.
	 * 
	 * @param fadeOutTime
	 */
	public void switchScreen(float fadeOutTime) {
		getRoot().getColor().a = 1;
		final SequenceAction sequenceAction = new SequenceAction();
		sequenceAction.addAction(Actions.fadeOut(fadeOutTime));
		sequenceAction.addAction(Actions.run(new Runnable() {
			@Override
			public void run() {
				PaCGame.getInstance().setScreen(new WorldScreen());
			}
		}));
		getRoot().addAction(sequenceAction);
		/*
		 * backgroundImage.getColor().a = 1; SequenceAction sequenceAction2 =
		 * new SequenceAction(); sequenceAction2.addAction(
		 * Actions.fadeOut(fadeOutTime) );
		 * backgroundImage.addAction(sequenceAction2);
		 */
	}
}
