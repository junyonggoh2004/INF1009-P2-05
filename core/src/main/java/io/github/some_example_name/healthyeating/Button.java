package io.github.some_example_name.healthyeating;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * A reusable UI button with hover and click feedback.
 * Can display text, a solid color, or a texture image.
 *
 * Demonstrates encapsulation — all button logic (hit detection,
 * hover state, rendering) is contained in one class.
 */
public class Button {

    private float x, y, width, height;
    private String text;
    private Color normalColor;
    private Color hoverColor;
    private Color textColor;
    private Texture texture;
    private boolean hovered = false;
    private boolean visible = true;
    private final GlyphLayout layout = new GlyphLayout();

    /**
     * Creates a button with solid color background.
     */
    public Button(float x, float y, float width, float height,
                  String text, Color normal, Color hover) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.normalColor = normal;
        this.hoverColor = hover;
        this.textColor = Color.WHITE;
        this.texture = null;
    }

    /**
     * Creates a button with a texture image.
     */
    public Button(float x, float y, float width, float height,
                  String text, Texture texture, Color hover) {
        this(x, y, width, height, text, Color.GRAY, hover);
        this.texture = texture;
    }

    /**
     * Updates hover state based on mouse position.
     * Must be called every frame before draw().
     */
    public void update() {
        if (!visible) {
            hovered = false;
            return;
        }
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        hovered = (mx >= x && mx <= x + width && my >= y && my <= y + height);
    }

    /**
     * Draws the button as a solid color rectangle.
     * Call between shapeRenderer.begin(Filled) and end().
     */
    public void drawBackground(ShapeRenderer shapeRenderer) {
        if (!visible) return;

        Color bg = hovered ? hoverColor : normalColor;
        float drawX = x, drawY = y, drawW = width, drawH = height;

        if (hovered) {
            float grow = 3f;
            drawX -= grow;
            drawY -= grow;
            drawW += grow * 2;
            drawH += grow * 2;
        }

        shapeRenderer.setColor(bg);
        shapeRenderer.rect(drawX, drawY, drawW, drawH);
    }

    /**
     * Draws the button with texture and/or text.
     * Call between batch.begin() and end().
     */
    public void drawWithBatch(SpriteBatch batch, BitmapFont font) {
        if (!visible) return;

        float drawX = x, drawY = y, drawW = width, drawH = height;
        if (hovered) {
            float grow = 3f;
            drawX -= grow;
            drawY -= grow;
            drawW += grow * 2;
            drawH += grow * 2;
        }

        if (texture != null) {
            if (hovered) {
                batch.setColor(0.85f, 0.85f, 0.85f, 1f);
            } else {
                batch.setColor(1f, 1f, 1f, 1f);
            }
            batch.draw(texture, drawX, drawY, drawW, drawH);
            batch.setColor(1f, 1f, 1f, 1f);
        }

        if (text != null && !text.isEmpty()) {
            font.setColor(textColor);
            layout.setText(font, text);
            float textX = x + (width - layout.width) / 2f;
            float textY = y + (height + layout.height) / 2f;
            font.draw(batch, text, textX, textY);
        }
    }

    /**
     * Returns true if the button was just clicked this frame.
     */
    public boolean isClicked() {
        return visible && hovered && Gdx.input.justTouched();
    }

    // Setters
    public void setPosition(float x, float y) { this.x = x; this.y = y; }
    public void setSize(float w, float h) { this.width = w; this.height = h; }
    public void setText(String text) { this.text = text; }
    public void setTextColor(Color c) { this.textColor = c; }
    public void setNormalColor(Color c) { this.normalColor = c; }
    public void setHoverColor(Color c) { this.hoverColor = c; }
    public void setTexture(Texture t) { this.texture = t; }
    public void setVisible(boolean v) { this.visible = v; }

    // Getters
    public boolean isHovered() { return hovered; }
    public boolean isVisible() { return visible; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
}