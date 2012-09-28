# Katapakote for Android

This is a port of the Katapakote app for Android, from the original version for iOS.

Katapakote for iOS: http://itunes.apple.com/br/app/katapakote+/id462566626?mt=8

# About the app
Katapakote is a package tracking application that integrates directly into the Brazilian Postal Service. It's meant to help online shoppers and sellers in tracking multiple applications at the same time in an easy to use interface.

# Class Description

## Top-level objects

**- DB Class**

This class is responsible for:

- Setting up the database on the device in initial use, from a previously configured database file
- Handling connection with SQLite
- Providing access to the data contained in the database

**- SessionManager**

This class implements the concept of a Singleton class. It is used to hold data that needs to be accessed across the entire application (or a session), enhancing code encapsulation and separating business logic from interface setup.

This class is responsible for:

- Activating the DB class
- Communicating to the database
- Connecting to the postal service API by making async HTTP GET requests
- Parsing XML responses and adding them to the database
- Providing package and status data in format that's easy to work with (HashMap)

## Activities

**- MainActivity : ListActivity**

This class shows the first screen of the app that contains a list of your packages. It also provides a BaseAdapter extension (ListAdapter) that loads custom rows into the list.

**- DetailActivity : ListActivity**

This class shoes a list of statuses for a given package (selected in MainActivity)

**- AddPackageActivity : Activity**

This activity handles the addition of packages to the database. It contains a text mask according to business rules to keep data integrity.

Also contains a QR/barcode reader integration with zxing barcode reader

Sample QR Code: http://api.qrserver.com/v1/create-qr-code/?data=XX232444555XX&size=250x250
Barcode Reader app: https://play.google.com/store/apps/details?id=com.google.zxing.client.android&hl=en

## Interface resources

**- drawable/dropshadow**

Creates a dropshadow effect on a given view

**- drawable/gradient**

Contains the gradient used in the navigation bar across the entire application

**- drawable/background_pattern**

Contains the background pattern used in the back of the main views across the entire application

**- drawable/border**

Contains a dark brown border used on AddPackageActivity

**- layout/package_cell**

Contains the custom cell used in MainActivity

----------------------------------------

Copyright 2012 Appkraft and Thiago Peres
http://appkraft.net/

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.