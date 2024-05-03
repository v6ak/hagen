package com.v6ak.hagen.output

import com.v6ak.hagen.haCalls.ServiceId

object CountersTopLevel extends MapDomainDefinition(Counters, "counter", ServiceId.ReloadAll /*TODO: really?*/)
