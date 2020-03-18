# re-frame-window-fx

[![Clojars Project](https://img.shields.io/clojars/v/district0x/re-frame-window-fx.svg)](https://clojars.org/district0x/re-frame-window-fx)
[![CircleCI](https://circleci.com/gh/district0x/re-frame-window-fx.svg?style=svg)](https://circleci.com/gh/district0x/re-frame-window-fx)

[re-frame](https://github.com/Day8/re-frame) effect handlers related to browser window.

## Installation
Add `[district0x/re-frame-window-fx "VERSION"]` into your project.clj    
Include `[district0x.re-frame.window-fx]` in your CLJS file

## API Overview

- [:window/on-resize](#windowon-resize)
- [:window/stop-on-resize](#windowstop-on-resize)
- [:window/on-focus](#windowon-focus)
- [:window/stop-on-focus](#windowstop-on-focus)
- [:window/on-blur](#windowon-blur)
- [:window/stop-on-blur](#windowstop-on-blur)
- [:window/on-hashchange](#windowon-hashchange)
- [:window/stop-on-hashchange](#windowstop-on-hashchange)
- [:window/scroll-to](#windowscroll-to)

## Usage
#### `:window/on-resize`
Fires `:dispatch` when browser window is resized. Can set `:debounce-ms` for debouncing event, to prevent too frequent firings.  
Default `:debounce-ms` is 166ms.  
Dispatched event receives `[js/window.innerWidth js/window.innerHeight]` as a last arg. 
```clojure
(reg-event-fx
  ::my-event
  (fn []
    {:window/on-resize {:dispatch [::window-resized]
                        :debounce-ms 200
                        ;; :id is optional
                        :id ::my-listener}}))
```
#### `:window/stop-on-resize`
If you passed `:id` into event handler above, you can stop listening by that `:id`
```clojure
(reg-event-fx
  ::my-event
  (fn []
    {:window/stop-on-resize {:id ::my-listener}}))
```

#### `:window/on-focus`
Fires `:dispatch` when tab/window receives focus
```clojure
(reg-event-fx
  ::my-event
  (fn []
    {:window/on-focus {:dispatch [::window-focused]
                       ;; :id is optional
                       :id ::my-listener}}))
```
#### `:window/stop-on-focus`
If you passed `:id` into event handler above, you can stop listening by that `:id`

#### `:window/on-blur`
Fires `:dispatch` when tab/window was blurred
```clojure
(reg-event-fx
  ::my-event
  (fn []
    {:window/on-blur {:dispatch [::window-blurred]
                      ;; :id is optional
                      :id ::my-listener}}))
```
#### `:window/stop-on-blur`
If you passed `:id` into event handler above, you can stop listening by that `:id`

#### `:window/on-hashchange`
Fires `:dispatch` when location hash was changed
```clojure
(reg-event-fx
  ::my-event
  (fn []
    {:window/on-hashchange {:dispatch [[::window-hashchanged]
                                       ;; :id is optional
                                       :id ::my-listener]}}))
```

#### `:window/stop-on-hashchange`
If you passed `:id` into event handler above, you can stop listening by that `:id`

#### `:window/scroll-to`
Scroll window into x y coordinates
```clojure
(reg-event-fx
  ::my-event
  (fn []
    {:window/scroll-to [100 200]}))
```
## Development
```bash
lein deps

# To run tests and rerun on changes
lein doo chrome tests
```
