package com.v6ak.hagen.hardware.ikea.somrig

case class SomrigAction(button: SomrigButton, action: SomrigButtonAction):
  def name: String = s"${button}_$action"

  override def toString: String = name

object SomrigAction:
  abstract sealed class SomrigActionFactory(button: SomrigButton):
    def InitialPress: SomrigAction = SomrigAction(button, SomrigButtonAction.InitialPress)

    def LongPress: SomrigAction = SomrigAction(button, SomrigButtonAction.LongPress)

    def ShortRelease: SomrigAction = SomrigAction(button, SomrigButtonAction.ShortRelease)

    def LongRelease: SomrigAction = SomrigAction(button, SomrigButtonAction.LongRelease)

    def DoublePress: SomrigAction = SomrigAction(button, SomrigButtonAction.DoublePress)

  object One extends SomrigActionFactory(SomrigButton.One)

  object Two extends SomrigActionFactory(SomrigButton.Two)
