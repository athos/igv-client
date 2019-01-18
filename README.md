# igv-client

Clojure implementation of IGV client to control [IGV](http://software.broadinstitute.org/software/igv/) via its [Port Commands](http://software.broadinstitute.org/software/igv/automation#PORTCOMMANDS)

## Installation

Add the following to your `:dependencies`:

```
[igv-client "0.1.0"]
```

## Usage

```
(require '[igv-client.core :as igv])

(def client (igv/connect "127.0.0.1" 60151))
(igv/set-snapshot-dir! client "/path/to/snapshot/dir")

(igv/reset client)
(igv/load-file client "/path/to/your/input/file")
(igv/goto client "chr1" 123456)
(igv/collapse client)
(igv/snapshot client)
```

## License

Copyright © 2019 Shogo Ohta

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
