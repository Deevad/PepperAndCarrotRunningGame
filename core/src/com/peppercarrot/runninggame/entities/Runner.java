package com.peppercarrot.runninggame.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.nGame.utils.scene2d.AnimatedImage;
import com.peppercarrot.runninggame.utils.Assets;
import com.peppercarrot.runninggame.utils.Constants;
import com.peppercarrot.runninggame.world.Platform;
import com.peppercarrot.runninggame.world.collision.IEnemyCollisionAwareActor;
import com.peppercarrot.runninggame.world.collision.IPlatformCollisionAwareActor;
import com.peppercarrot.runninggame.world.collision.IPotionCollisionAwareActor;

/**
 * Playable character class. The runner is only able to move horizontally, other
 * entities are moving towards player. Runner can jump and doublejump.
 * 
 * @author WinterLicht
 *
 */
public abstract class Runner extends Group
		implements IPlatformCollisionAwareActor, IEnemyCollisionAwareActor, IPotionCollisionAwareActor {
	String name;
	public State currState = State.RUNNING;
	public Pet pet;
	int speedY = 0;
	/** Vertical speed in pixel. */
	int maxJumpSpeed = 24;
	/** Maximum speed when jumping in pixel */

	public Image runnerImage;
	AnimatedImage runningAnim;
	AnimatedImage jumpingAnim;
	AnimatedImage doubleJumpingAnim;
	AnimatedImage fallingAnim;
	AnimatedImage attackingAnim;

	public Ability ability1;
	public Ability ability2;
	public Ability ability3;

	/**
	 * Possible states.
	 */
	enum State {
		RUNNING, FALLING, JUMPING, DOUBLEJUMPING,
		// TODO: for this states may be an other animation
		ATTACK_RUNNING, ATTACK_FALLING, ATTACK_JUMPING, ATTACK_DOUBLEJUMPING, DYING;
	}

	public Runner(String name) {
		this.name = name;
		runnerImage = new Image(new TextureRegion(Assets.I.atlas.findRegion(name + "_run")));
		addActor(runnerImage);
		initAbilities();
		initAnimations();
		initPet();
		// Runner is always placed with some offset
		setOrigin(Align.center);
		setX(Constants.OFFSET_TO_EDGE);
		setY(Constants.OFFSET_TO_GROUND);
	}

	protected abstract void initAbilities();
	protected abstract void initAnimations();
	protected abstract void initPet();

	public void jump() {
		if (isRunnig()) {
			setJumping();
			speedY = maxJumpSpeed;
		} else if (isJumping()) {
			setDoubleJumping();
			speedY = maxJumpSpeed;
		}
	}

	/**
	 * Land on given y-coordinate position.
	 * 
	 * @param y
	 */
	public void land(float y) {
		// Player lands only if his speed is small enough
		final int speedOffset = 8;
		if (speedY < speedOffset) {
			setY(y);
			speedY = 0;
			setRunnig();
		}
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		// Update all attacks
		ability1.update(delta);
		ability2.update(delta);
		ability3.update(delta);
		// Decide which animation is displayed
		switch (currState) {
		case DOUBLEJUMPING:
			doubleJumpingAnim.act(delta);
			runnerImage.setDrawable(doubleJumpingAnim.getDrawable());
			break;
		case DYING:
			break;
		case JUMPING:
			jumpingAnim.act(delta);
			runnerImage.setDrawable(jumpingAnim.getDrawable());
			break;
		case RUNNING:
			runningAnim.act(delta);
			runnerImage.setDrawable(runningAnim.getDrawable());
			break;
		case FALLING:
			fallingAnim.act(delta);
			runnerImage.setDrawable(fallingAnim.getDrawable());
			break;
		case ATTACK_RUNNING:
			attackingAnim.act(delta);
			runnerImage.setDrawable(attackingAnim.getDrawable());
			break;
		case ATTACK_JUMPING:
			attackingAnim.act(delta);
			runnerImage.setDrawable(attackingAnim.getDrawable());
			break;
		case ATTACK_DOUBLEJUMPING:
			attackingAnim.act(delta);
			runnerImage.setDrawable(attackingAnim.getDrawable());
			break;
		case ATTACK_FALLING:
			attackingAnim.act(delta);
			runnerImage.setDrawable(attackingAnim.getDrawable());
			break;
		default: // Should not be reached
			break;
		}
		// Gravity is 1 pixel
		speedY -= 1;
		// Move
		final float oldYPos = getY();
		setY(getY() + speedY);
		if (getY() < oldYPos && !isAttacking()) {
			// Player is falling, if his y-position is lowered
			// and he was previously running.
			setFalling();
		}
		// Player can't fall under/below the ground
		if (getY() < Constants.OFFSET_TO_GROUND) {
			land(Constants.OFFSET_TO_GROUND);
			setRunnig();
			setY(Constants.OFFSET_TO_GROUND);
		}
	}

	// Helper methods for states
	public void setRunnig() {
		if (isAttacking())
			currState = State.ATTACK_RUNNING;
		else
			currState = State.RUNNING;
	}

	public void setFalling() {
		if (isAttacking())
			currState = State.ATTACK_FALLING;
		else
			currState = State.FALLING;
	}

	public void setJumping() {
		if (isAttacking())
			currState = State.ATTACK_JUMPING;
		else
			currState = State.JUMPING;
	}

	public void setDoubleJumping() {
		if (isAttacking())
			currState = State.ATTACK_DOUBLEJUMPING;
		else
			currState = State.DOUBLEJUMPING;
	}

	public void setDying() {
		currState = State.DYING;
	}

	/**
	 * resets also attacking animation.
	 */
	public void setAttacking() {
		attackingAnim.reset();
		switch (currState) {
		case DOUBLEJUMPING:
			currState = State.ATTACK_DOUBLEJUMPING;
			break;
		case FALLING:
			currState = State.ATTACK_FALLING;
			break;
		case JUMPING:
			currState = State.ATTACK_JUMPING;
			break;
		case RUNNING:
			currState = State.ATTACK_RUNNING;
			break;
		default:
			break;
		}
	}

	public boolean isAttacking() {
		return (currState == State.ATTACK_DOUBLEJUMPING || currState == State.ATTACK_RUNNING
				|| currState == State.ATTACK_FALLING || currState == State.ATTACK_JUMPING);
	}

	public boolean isRunnig() {
		return (currState == State.RUNNING || currState == State.ATTACK_RUNNING);
	}

	public boolean isFalling() {
		return (currState == State.FALLING || currState == State.ATTACK_FALLING);
	}

	public boolean isJumping() {
		return (currState == State.JUMPING || currState == State.ATTACK_JUMPING);
	}

	public boolean isDoubleJumping() {
		return (currState == State.DOUBLEJUMPING || currState == State.ATTACK_DOUBLEJUMPING);
	}

	public boolean isDying() {
		return (currState == State.DYING);
	}

	public State getCurrentState() {
		return currState;
	}

	public void setState(State state) {
		currState = state;
	}

	@Override
	public void retrieveHitbox(Rectangle rectangle) {
		final int offset = 30;

		// slightly smaller hitbox of the player as his sprite.
		rectangle.x = getX() + offset;
		rectangle.y = getY() + offset;
		rectangle.width = runnerImage.getWidth() - offset * 2;
		rectangle.height = runnerImage.getHeight() - offset * 2;
	}

	@Override
	public boolean onHitPotion(Potion potion) {
		if (potion.isVisible()) {
			potion.collected();
			ability1.increaseEnergy(1);
			ability2.increaseEnergy(1);
			ability3.increaseEnergy(1);
			return true;
		}
		return false;
	}

	@Override
	public boolean onHitEnemy(Enemy enemy) {
		if (enemy.isAlive()) {
			//TODO auskommentieren
			//setDying();
			return true;
		}
		return false;
	}

	@Override
	public float getPlatformCollisionX() {
		return getX();
	}

	@Override
	public float getPlatformCollisionY() {
		return getY();
	}

	@Override
	public boolean onHitPlatform(Platform platform, float platformHitTop) {
		land(platformHitTop);
		return true;
	}
}
