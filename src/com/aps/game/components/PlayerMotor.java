package com.aps.game.components;

import com.aps.engine.util.Mathf;
import com.aps.engine.util.Vector2;
import com.aps.game.LayerMask;
import com.aps.game.Physics;

public class PlayerMotor extends Component {
	private RectCollider collider;
	private Rigidbody rigidbody;

	private boolean previouslyGrounded;
	private boolean grounded;
	private boolean canDoubleJump = true;
	private float jumpSpeed = 0;
	private boolean jump = false;
	private boolean isJumping = false;
	private float widthOffset = 0.9f;
	private float groundCheckDistance = 0.015f;
	private LayerMask groundMask = new LayerMask(0);
	
	public void awake() {
		collider = gameObject.createComponent(RectCollider.class);
		collider.setProperties(new Vector2(-0.03125f, 0f), new Vector2(0.53125f, 0.96875f));
		rigidbody = gameObject.createComponent(Rigidbody.class);
	}
	
	public void physicsUpdate() {
		checkGround();
		
		if (grounded || canDoubleJump) {
			if (grounded) {
				canDoubleJump = true;
				isJumping = false;
			}
			
			if (jump) {
				rigidbody.setVerticalFriction(1f);
				isJumping = true;
				if (canDoubleJump && !grounded) {
					canDoubleJump = false;
					rigidbody.setVelocity(new Vector2(rigidbody.getVelocity().getX(), 0f));
					rigidbody.accelerate(new Vector2(0, -jumpSpeed));
				} else {
					rigidbody.accelerate(new Vector2(0, -jumpSpeed));
				}
			}
		}
		
		jump = false;
	}
	
	public void move(float speed) {
		rigidbody.setVelocity(new Vector2(speed, rigidbody.getVelocity().getY()));
		
		if (speed == 0) {
			rigidbody.setHorizontalFriction(0f);
			rigidbody.setVerticalFriction(!grounded || isJumping ? 1f : 0f);
		} else {
			rigidbody.setHorizontalFriction(0.9f);
			rigidbody.setVerticalFriction(1f);
		}
	}
	
	public void jump(float speed) {
		jump = true;
		jumpSpeed = speed;
	}
	
	private void checkGround() {
		previouslyGrounded = grounded;
		
		if (Physics.rectcast(collider.getGlobalCenter().add(0, ((RectCollider)collider).getHalfSize().getY() - groundCheckDistance / 2 + 0.001f), ((RectCollider)collider).getSize().getX() * widthOffset, groundCheckDistance, groundMask).getColliders().length > 0) {
			grounded = true;
		} else {
			grounded = false;
		}
	}

	public RectCollider getCollider() {
		return collider;
	}
}
