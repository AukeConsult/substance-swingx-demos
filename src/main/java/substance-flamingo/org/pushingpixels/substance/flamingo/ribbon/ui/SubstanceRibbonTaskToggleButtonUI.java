/*
 * Copyright (c) 2005-2010 Flamingo / Substance Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of Flamingo Kirill Grouchnikov nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.pushingpixels.substance.flamingo.ribbon.ui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;

import org.pushingpixels.flamingo.api.common.AbstractCommandButton;
import org.pushingpixels.flamingo.api.ribbon.RibbonContextualTaskGroup;
import org.pushingpixels.flamingo.internal.ui.ribbon.BasicRibbonTaskToggleButtonUI;
import org.pushingpixels.flamingo.internal.ui.ribbon.JRibbonTaskToggleButton;
import org.pushingpixels.substance.api.*;
import org.pushingpixels.substance.api.shaper.SubstanceButtonShaper;
import org.pushingpixels.substance.flamingo.ribbon.RibbonBackgroundDelegate;
import org.pushingpixels.substance.flamingo.utils.CommandButtonVisualStateTracker;
import org.pushingpixels.substance.internal.animation.StateTransitionTracker;
import org.pushingpixels.substance.internal.animation.TransitionAwareUI;
import org.pushingpixels.substance.internal.painter.DecorationPainterUtils;
import org.pushingpixels.substance.internal.utils.*;

/**
 * UI for {@link JRibbonTaskToggleButton} components in <b>Substance</b> look
 * and feel.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceRibbonTaskToggleButtonUI extends
		BasicRibbonTaskToggleButtonUI implements TransitionAwareUI {
	/**
	 * Tracker for visual state transitions.
	 */
	protected CommandButtonVisualStateTracker substanceVisualStateTracker;

	/**
	 * Property change listener on the button.
	 */
	private PropertyChangeListener substancePropertyChangeListener;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
	 */
	public static ComponentUI createUI(JComponent comp) {
		return new SubstanceRibbonTaskToggleButtonUI();
	}

	/**
	 * Painting delegate.
	 */
	private RibbonBackgroundDelegate delegate;

	/**
	 * Simple constructor.
	 */
	public SubstanceRibbonTaskToggleButtonUI() {
		this.delegate = new RibbonBackgroundDelegate();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();
		this.commandButton.setOpaque(false);
		this.commandButton.setBorder(new Border() {
			public Insets getBorderInsets(Component c) {
				return new Insets(0, 12, 0, 12);
			}

			public boolean isBorderOpaque() {
				return false;
			}

			public void paintBorder(Component c, Graphics g, int x, int y,
					int width, int height) {
			}
		});
		SubstanceLookAndFeel.setDecorationType(this.commandButton,
				DecorationAreaType.GENERAL);

		this.commandButton.putClientProperty(
				SubstanceLookAndFeel.COLORIZATION_FACTOR,
				RibbonContextualTaskGroup.HUE_ALPHA);
	}

	@Override
	protected void uninstallComponents() {
		DecorationPainterUtils.clearDecorationType(this.commandButton);
		super.uninstallDefaults();
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		this.substanceVisualStateTracker = new CommandButtonVisualStateTracker();
		this.substanceVisualStateTracker.installListeners(this.commandButton);

		this.substancePropertyChangeListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("contextualGroupHueColor".equals(evt.getPropertyName())) {
					Color newValue = (Color) evt.getNewValue();
					commandButton.setBackground(newValue);
				}
			}
		};
		this.commandButton
				.addPropertyChangeListener(this.substancePropertyChangeListener);
	}

	@Override
	protected void uninstallListeners() {
		this.substanceVisualStateTracker.uninstallListeners(this.commandButton);
		this.substanceVisualStateTracker = null;

		this.commandButton
				.removePropertyChangeListener(this.substancePropertyChangeListener);
		this.substancePropertyChangeListener = null;

		super.uninstallListeners();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicToggleButtonUI#paint(java.awt.Graphics,
	 * javax.swing.JComponent)
	 */
	@Override
	public void paint(Graphics g, JComponent c) {
		this.layoutInfo = this.layoutManager.getLayoutInfo(this.commandButton,
				g);

		this.delegate.updateTaskToggleButtonBackground(g,
				(JRibbonTaskToggleButton) this.commandButton);
		this.paintText(g);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.flamingo.ribbon.ui.BasicToggleTabButtonUI#update(java.awt.Graphics
	 * , javax.swing.JComponent)
	 */
	@Override
	public void update(Graphics g, JComponent c) {
		this.paint(g, c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.flamingo.ribbon.ui.BasicRibbonTaskToggleButtonUI#paintText(
	 * java.awt.Graphics)
	 */
	@Override
	protected void paintText(Graphics g) {
		FontMetrics fm = g.getFontMetrics();
		String toPaint = this.commandButton.getText();

		// compute the insets
		int fullInsets = this.commandButton.getInsets().left;
		int pw = this.getPreferredSize(this.commandButton).width;
		int mw = this.getMinimumSize(this.commandButton).width;
		int w = this.commandButton.getWidth();
		int h = this.commandButton.getHeight();
		int insets = fullInsets - (pw - w) * (fullInsets - 2) / (pw - mw);

		// and the text rectangle
		Rectangle textRect = new Rectangle(insets,
				1 + (h - fm.getHeight()) / 2, w - 2 * insets, fm.getHeight());

		// show the first characters that fit into the available text rectangle
		while (true) {
			if (toPaint.length() == 0)
				break;
			int strWidth = fm.stringWidth(toPaint);
			if (strWidth <= textRect.width)
				break;
			toPaint = toPaint.substring(0, toPaint.length() - 1);
		}

		int finalStrWidth = fm.stringWidth(toPaint);
		if (finalStrWidth < textRect.width) {
			int delta = textRect.width - finalStrWidth;
			textRect.x += delta / 2;
			textRect.width -= delta;
		}

		StateTransitionTracker.ModelStateInfo modelStateInfo = this.substanceVisualStateTracker
				.getActionStateTransitionTracker().getModelStateInfo();
		ComponentState currState = modelStateInfo.getCurrModelState();
		float buttonAlpha = SubstanceColorSchemeUtilities.getAlpha(
				this.commandButton, currState);

		Color fgColor = getForegroundColor(this.commandButton, modelStateInfo);

		if (buttonAlpha < 1.0f) {
			Color bgFillColor = SubstanceColorUtilities
					.getBackgroundFillColor(this.commandButton);
			fgColor = SubstanceColorUtilities.getInterpolatedColor(fgColor,
					bgFillColor, buttonAlpha);
		}

		SubstanceTextUtilities.paintText(g, this.commandButton, textRect,
				toPaint, -1, this.commandButton.getFont(), fgColor, null);
	}

	private static Color getForegroundColor(AbstractCommandButton button,
			StateTransitionTracker.ModelStateInfo modelStateInfo) {
		ComponentState currState = modelStateInfo.getCurrModelState();
		Map<ComponentState, StateTransitionTracker.StateContributionInfo> activeStates = modelStateInfo
				.getStateContributionMap();

		SubstanceColorScheme colorScheme = SubstanceColorSchemeUtilities
				.getColorScheme(
						button.isFlat() && currState == ComponentState.ENABLED ? button
								.getParent() : button,
						ColorSchemeAssociationKind.TAB, currState);
		if (currState.isDisabled() || (activeStates == null)
				|| (activeStates.size() == 1)) {
			return colorScheme.getForegroundColor();
		}

		float aggrRed = 0;
		float aggrGreen = 0;
		float aggrBlue = 0;
		for (Map.Entry<ComponentState, StateTransitionTracker.StateContributionInfo> activeEntry : activeStates
				.entrySet()) {
			ComponentState activeState = activeEntry.getKey();
			float alpha = activeEntry.getValue().getContribution();
			SubstanceColorScheme activeColorScheme = SubstanceColorSchemeUtilities
					.getColorScheme(
							button.isFlat()
									&& activeState == ComponentState.ENABLED ? button
									.getParent() : button,
							ColorSchemeAssociationKind.TAB, activeState);
			Color activeForeground = activeColorScheme.getForegroundColor();
			aggrRed += alpha * activeForeground.getRed();
			aggrGreen += alpha * activeForeground.getGreen();
			aggrBlue += alpha * activeForeground.getBlue();
		}
		return new Color((int) aggrRed, (int) aggrGreen, (int) aggrBlue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.ComponentUI#getPreferredSize(javax.swing.JComponent)
	 */
	@Override
	public Dimension getPreferredSize(JComponent c) {
		AbstractCommandButton button = (AbstractCommandButton) c;
		SubstanceButtonShaper shaper = SubstanceCoreUtilities
				.getButtonShaper(button);

		Dimension superPref = super.getPreferredSize(button);
		if (superPref == null)
			return null;

		if (shaper == null)
			return superPref;

		JButton dummy = new JButton(button.getText(), button.getIcon());
		return shaper.getPreferredSize(dummy, superPref);
	}

	@Override
	public StateTransitionTracker getTransitionTracker() {
		return this.substanceVisualStateTracker
				.getActionStateTransitionTracker();
	}

	@Override
	public boolean isInside(MouseEvent me) {
		return true;
	}
}
