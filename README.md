Bluetooth Library
=================

##About
Communication via Bluetooth is very used, thought it created a library that facilitates the use of this type of communication. The devices exchanging messages can be clients or servers. 
Servers await some connection, since the Client looking for the connection. Besides the library project put an example, implement the chat.

##Running
First it is necessary to add the permissions to use the Bluetooth in AndroidManifest:
```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
```

Initially I'll demonstrate how to get the server communication. To do that Bluetooth should be visible without it is not possible to continue. To do this use case:
```java
Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, BT_TIMER_VISIBLE); 
startActivityForResult(discoverableIntent, Constants.BT_VISIBLE); 
```

We will have to implement the onActivityResult method:
```java
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);

	switch (requestCode) {
		case BT_VISIBLE:
			if (resultCode == Constants.BT_TIMER_VISIBLE) {
				communicationBusinessLogic.stopCommucanition();
				communicationBusinessLogic.startServer();
			} 

			break;
	}
}
```

To begin with we Client using:
```java
communicationBusinessLogic.startFoundDevices();
```

To end communication exiting the application do:
```java
@Override
protected void onDestroy() {
	super.onDestroy();
		
	communicationBusinessLogic.unregisterFilter();
	communicationBusinessLogic.stopCommucanition();
}
```

##Author
Marcus Vinícius Pimenta  
email: [mvinicius.pimenta@gmail.com](mailto:mvinicius.pimenta@gmail.com)

License
-------

    Copyright (C) 2011 readyState Software Ltd
    Copyright (C) 2007 The Android Open Source Project

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

 [1]: http://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.readystatesoftware.sqliteasset&a=sqliteassethelper&v=LATEST&&c=jar
