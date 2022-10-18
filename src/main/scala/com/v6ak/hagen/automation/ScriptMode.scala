package com.v6ak.hagen.automation

enum ScriptMode(val name: String):
  // https://www.home-assistant.io/integrations/script/#script-modes
  case Single extends ScriptMode("single")
  case Restart extends ScriptMode("restart")
  case Queued extends ScriptMode("queued")
  case Parallel extends ScriptMode("parallel")