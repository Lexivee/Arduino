/*
 * This file is part of Arduino.
 *
 * Copyright 2015 Arduino LLC (http://www.arduino.cc/)
 *
 * Arduino is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * As a special exception, you may use this file as part of a free software
 * library without restriction.  Specifically, if other files instantiate
 * templates or use macros or inline functions from this file, or you compile
 * this file and link it with other files to produce an executable, this
 * file does not by itself cause the resulting executable to be covered by
 * the GNU General Public License.  This exception does not however
 * invalidate any other reasons why the executable file might be covered by
 * the GNU General Public License.
 */

package cc.arduino.view.preferences;

import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import processing.app.Base;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Collection;

import static processing.app.I18n.getString;

public class AdditionalBoardsManagerURLTextArea extends javax.swing.JDialog {

  private ActionListener onOkListener;

  public AdditionalBoardsManagerURLTextArea(Window parent) {
    super(parent);
    initComponents();
    setLocationRelativeTo(parent);

    Base.registerWindowCloseKeys(getRootPane(), this::cancelActionPerformed);
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    javax.swing.JButton cancel = new javax.swing.JButton();
    javax.swing.JButton ok = new javax.swing.JButton();
    javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
    unofficialListURLLabel = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(getString("Additional Boards Manager URLs"));
    setModal(true);
    setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);

    additionalBoardsManagerURLs.setColumns(20);
    additionalBoardsManagerURLs.setRows(5);
    additionalBoardsManagerURLs.setName(""); // NOI18N
    jScrollPane1.setViewportView(additionalBoardsManagerURLs);

    cancel.setText(getString("Cancel"));
    cancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancelActionPerformed(evt);
      }
    });

    ok.setText(getString("OK"));
    ok.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        okActionPerformed(evt);
      }
    });

    jLabel1.setText(getString("Enter additional URLs, one for each row"));

    unofficialListURLLabel.setText(_("Click for a list of unofficial boards support URLs"));
    unofficialListURLLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    unofficialListURLLabel.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        unofficialListURLLabelMouseClicked(evt);
      }

      public void mouseExited(java.awt.event.MouseEvent evt) {
        unofficialListURLLabelMouseExited(evt);
      }

      public void mouseEntered(java.awt.event.MouseEvent evt) {
        unofficialListURLLabelMouseEntered(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
          .addContainerGap()
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
              .addGap(0, 0, Short.MAX_VALUE)
              .addComponent(ok)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(cancel))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel1)
                .addComponent(unofficialListURLLabel))
              .addGap(0, 0, Short.MAX_VALUE)))
          .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
          .addContainerGap()
          .addComponent(jLabel1)
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
          .addComponent(unofficialListURLLabel)
          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(ok)
            .addComponent(cancel))
          .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
    dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
  }//GEN-LAST:event_cancelActionPerformed

  private void okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okActionPerformed
    ActionEvent actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "");
    onOkListener.actionPerformed(actionEvent);
    cancelActionPerformed(evt);
  }//GEN-LAST:event_okActionPerformed

  private void unofficialListURLLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_unofficialListURLLabelMouseEntered
    unofficialListURLLabel.setForeground(new Color(0, 0, 140));
  }//GEN-LAST:event_unofficialListURLLabelMouseEntered

  private void unofficialListURLLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_unofficialListURLLabelMouseExited
    unofficialListURLLabel.setForeground(new Color(76, 76, 76));
  }//GEN-LAST:event_unofficialListURLLabelMouseExited

  private void unofficialListURLLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_unofficialListURLLabelMouseClicked
    Base.openURL("https://github.com/arduino/Arduino/wiki/Unofficial-list-of-3rd-party-boards-support-urls");
  }//GEN-LAST:event_unofficialListURLLabelMouseClicked

  public void setText(String text) {
    Collection<String> urls = splitAndTrim(text, ",");
    additionalBoardsManagerURLs.setText(Joiner.on("\n").skipNulls().join(urls));
  }

  private Collection<String> splitAndTrim(String text, String separator) {
    Collection<String> urls = Arrays.asList(text.split(separator));
    return FluentIterable.from(urls).transform(String::trim).filter(url -> !url.isEmpty()).toList();
  }

  public String getText() {
    Collection<String> urls = splitAndTrim(additionalBoardsManagerURLs.getText(), "\n");
    return Joiner.on(",").skipNulls().join(urls);
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private final javax.swing.JTextArea additionalBoardsManagerURLs = new javax.swing.JTextArea();
  private javax.swing.JLabel unofficialListURLLabel;
  // End of variables declaration//GEN-END:variables

  public void onOk(ActionListener listener) {
    this.onOkListener = listener;
  }
}
