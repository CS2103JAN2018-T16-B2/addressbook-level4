name = WScript.Arguments(0)
speaks="This is " + name
CreateObject("sapi.spvoice").Speak speaks
