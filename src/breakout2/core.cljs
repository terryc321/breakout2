(ns ^:figwheel-hooks breakout2.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]))

(println "This text is printed from src/canvas/core.cljs. Go ahead and edit it and see reloading in action.")

;; reagent-project/reagent-cookbook/recipes/canvas-fills-div example
(def status (reagent/atom ""))
(def tock (reagent/atom true))
(def batx (reagent/atom 150))
(def baty (reagent/atom 450))
(def ballx (reagent/atom 100))
(def bally (reagent/atom 300))
(def ballvx (reagent/atom 10))
(def ballvy (reagent/atom 5))
(def score (reagent/atom 0))
(def bricks (reagent/atom
                 (for [x (range 50 450 25)
                       y (range 50 150 20)]
                   {:x x :y y :x2 40 :y2 20})))




(def window-width (reagent/atom nil))

(defn on-window-resize [ evt ]
  (reset! window-width (.-innerWidth js/window))
  ;;(console.log "window width = " @window-width)
  )


;; (defn draw
;;   [radius canvas]
;;   (let [canvas-dim (* 2 radius)]
;;     ;; resize canvas
;;     (set! (.-width canvas) canvas-dim)
;;     (set! (.-height canvas) canvas-dim)
;;     ;; draw the shape
;;     (let [ctx (.getContext canvas "2d")
;;           center-x (/ (.-width canvas) 2)
;;           center-y (/ (.-height canvas) 2)]
;;       (set! (.-fillStyle ctx) "rgb(0,0,0)")
;;       (.clearRect ctx 0 0 (.-width canvas) (.-height canvas))
;;       (.beginPath ctx)
;;       (.arc ctx center-x center-y radius 0 (* 2 (.-PI js/Math)) false)
;;       (.fill ctx)
;;       (.stroke ctx))))



(defn tick [timestamp]
  (reset! ballx (+ @ballx @ballvx))
  (reset! bally (+ @bally @ballvy))

  (if (> @ballx 450)
    (do
      (reset! ballx 450)
      (reset! ballvx (- @ballvx))
      ))
  
  ;; bat missed the ball
  (if (> @bally 490)
    (do
      (reset! ballvy 0)
      (reset! ballvx 0)))

  ;; bat hit ball
  (if (and (> @bally 450)
           (<= @ballx (+ @batx 40))
           (>= @ballx (- @batx 40)))
    (do
      (reset! ballvy (- @ballvy))
      (reset! bally 449)
      ))
  
    
  (if (< @ballx 50)
    (do
      (reset! ballx 50)
      (reset! ballvx (- @ballvx))
      ))

  (if (< @bally 50)
    (do
      (reset! bally 50)
      (reset! ballvy (- @ballvy))
      ))
  
  (swap! tock not)
  ;;(console.log "ball x,y = " @ballx " , " @bally)
  )

(defn rand-rgb []
  (let [r (js/Math.floor (* 200 (js/Math.random 200))) g (js/Math.floor (* 200 (js/Math.random 200))) b (js/Math.floor (* 200 (js/Math.random 200))) ] (str  "rgb("  r  "," g "," b ")")))

(defn hit-brick? [brick]
  (and (> @ballx (brick :x))
       (< @ballx (+ 40  (brick :x)))
       (> @bally (brick :y))
       (< @bally (+ 20  (brick :y)))))


(defn rad-to-deg [r]
  (* 360 (/ r (* 2 Math.PI))))



(defn collision-action [brick]
  (let [dx (- @ballx (brick :x))
        dy (- @bally (brick :y))
        angle (if (zero? dy) 90 (rad-to-deg (Math.atan (/ dx dy))))
        ]
       
    ;; (console.log "collision angle = " angle " degrees")

    ;; (if (> (js/Math.abs dy) (js/Math.abs dx))
    ;;   (reset! ballvy (- @ballvy))
    ;;   (reset! ballvx (- @ballvx)))
 
    ;; ;; (reset! ballvy (- @ballvy))
    ;; ;; (reset! ballvx (- @ballvx))

    ;; @bricks
    ;; true

    (cond
      (<= (js/Math.abs angle) 45.0)
      (reset! ballvy (- @ballvy))

      true
      (reset! ballvx (- @ballvx))
      )

    
    ))









(defn collision? []
  (let [collide  (doall (filter (fn [brick] (hit-brick? brick))
                         @bricks))
        new-bricks (doall (filter (fn [brick] (not (hit-brick? brick)))
                         @bricks))]
    ;; lets assume all collision data recovered and score updated
    (if (empty? collide)
      nil
      (do
        (reset! bricks new-bricks)
        (collision-action (first collide))))))



(defn ^:export myMouseMove [ev]
  (let [x (.-clientX ev)]
    ;;(console.log "mouse x = " x)
    ;;(reset! status (str " mouse x = " x)) 
    (reset! batx x)))






                              


;; presumably draw something
(defn draw-canvas-contents [ canvas ]
  (let [ ctx (.getContext canvas "2d")
        w (.-clientWidth canvas)
        h (.-clientHeight canvas)
        center-x (/ w 2)
        center-y (/ h 2)]

    (set! (.-fillStyle ctx) "rgb(255,0,0)")
    ;;(.clearRect ctx 0 0 (.-width canvas) (.-height canvas))
    (.clearRect ctx 0 0 w h )
    
    ;; (.beginPath ctx)
    ;; ;;(.style "green")
    
    ;; (.moveTo ctx 0 0)
    ;; (.lineTo ctx w h)
    ;; (.moveTo ctx w 0)
    ;; (.lineTo ctx 0 h)
    ;; (.stroke ctx)
    
    ;; (.beginPath ctx)

    ;; ;;(.style "purple")
    ;; (set! (.-strokeStyle ctx) "rgb(0,0,255)")    
    ;; (.moveTo ctx 0 0)
    ;; (.lineTo ctx 200 100)
    ;; (.stroke ctx)

    ;; circle -- the ball
    (.beginPath ctx)
    ;;(set! (.-strokeStyle ctx) "rgb(0,255,0)")
    ;;(set! (.-fillStyle ctx) "rgb(255,255,255)")
    ;;(set! (.-fillStyle ctx) "white")
    (set! (.-fillStyle ctx) "yellow")
    (.arc ctx @ballx @bally 4 0 (* 2 js/Math.PI))
    ;;(.stroke ctx)
    (.fill ctx)


    ;; the bat
    (.beginPath ctx)
    (set! (.-fillStyle ctx) "blue")
    (.fillRect ctx (- @batx 40) @baty 80 20)
    

    
    ;; ;; text
    ;; (set! (.-font ctx) "30px Arial")    
    ;; (.fillText ctx "hello world" 10 50)

    ;; ;;@ballx
    ;; (set! (.-font ctx) "30px Serif")    
    ;; (.fillText ctx (str "x,y" @ballx "," @bally) center-x center-y)

    
    ;; (set! (.-lineWidth ctx) 10)    
    ;; (.strokeRect ctx 75 140 150 110)
    ;; (.fillRect ctx 130 190 40 60)

    ;;(set! (.-fillStyle ctx) "red")    

    ;; has there been a collision ?

    (set! (.-fillStyle ctx) "rgb(255,0,0)")        
    (doall
     (for [b @bricks]       
       (.fillRect ctx (b :x) (b :y) 20 10)))

    
    ;; (doall  ;; wrap for in a doall
    ;;  (for [x (range 50 450 50)
    ;;        y (range 50 450 50)]
    ;;    (for [b (list {:x x :y y :x2 40 :y2 20 }
    ;;              ;; {:x 200 :y 150 :x2 40 :y2 20 }
    ;;              ;; {:x 250 :y 150 :x2 40 :y2 20 }
    ;;              ;; {:x 300 :y 150 :x2 40 :y2 20 }
    ;;              )]
    ;;      (do 
    ;;        (.fillRect ctx (b :x) (b :y) (b :x2) (b :y2)))
    ;;      ))
    ;;  )

    ;; ;;(set! (.-strokeStyle ctx) "rgb(0,255,0)")
    ;; (set! (.-fillStyle ctx) "rgb(255,0,0)")
    ;; (.fillRect ctx 150 150 40 20)
    
    ;; ;;(set! (.-strokeStyle ctx) "rgb(0,255,0)")
    ;; (set! (.-fillStyle ctx) "rgb(0,255,0)")    
    ;; (.fillRect ctx 200 150 40 20)

    ;; (set! (.-fillStyle ctx) "rgb(0,0,255)")
    ;; (.fillRect ctx 250 150 40 20)

    
    ;; ;; Roof
    ;; (.moveTo ctx 50 140 )                 ;
    ;; (.lineTo ctx 150 60)
    ;; (.lineTo ctx 250 140)
    ;; (.closePath ctx 250 140)
    ;; (.stroke ctx)

    (.requestAnimationFrame js/window (fn [timestamp] (tick timestamp)))

  (collision?)
  
  
  ))


  




;; form-3 type reagent component
;; local state dom-node
(defn div-with-canvas [ ]
  (let [dom-node (reagent/atom nil)]
    (reagent/create-class
     {:component-did-update
      (fn [ this ]
        (draw-canvas-contents (.-firstChild @dom-node)))

      :component-did-mount
      (fn [ this ]
        (reset! dom-node (reagent/dom-node this))
        ;;(set! (js/document.getElementById :game-canvas)
        ;;(.addEventListener this "onmousedown" myMouseMove)
        ;;(.addEventListener (js/document.getElementById "mycanvas1") "click" myMouseMove)

        (let [elem (.getElementById js/document "mycanvas1")]
          (.addEventListener (.-firstChild @dom-node)
                           "click"
                           myMouseMove))
        
  
        )

      :reagent-render
      (fn [ ]
        @tock ;; trigger on clock        
        @window-width ;; Trigger re-render on window resizes
        [:div.with-canvas
         ;; reagent-render is called before the compoment mounts, so
         ;; protect against the null dom-node that occurs on the first
         ;; render
         ;; [:canvas (if-let [ node @dom-node ]
         ;;            {:width (.-clientWidth node)
         ;;             :height (.-clientHeight node)})]
         [:canvas (if-let [ node @dom-node ]
                    {:width 500
                     :height 500
                     :class "mycanvas"
                     :id "mycanvas1"
                     ;;:mouse-down #(mouse-over %)
                     ;;:mouse-move (fn [ev] (mouse-move ev))
                     :style {:background-color "black"
                             :cursor "none"}
                     })]

         ])})))



(defn multiply [a b] (* a b))

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Hello world!"}))
(def initialized false)



(defn get-app-element []
  (gdom/getElement "app"))

(defn hello-world []  
  [:div
   [:h1 (:text @app-state)]
   [:h3 "Edit this in src/canvas/core.cljs and watch it change!"]
   ;;[:canvas {:height 500 :width 500 :style {:background-color "chocolate" :border "1px solid #000000"}}]
   [div-with-canvas]
   [:p @status]
   ;;[:p "ball x y = " @ballx "," @bally]
   ])



(defn mount [el]
  (reagent/render-component [hello-world] el)
  (.addEventListener js/window "resize" on-window-resize)
  (set! (.. js/window -onmousemove) myMouseMove)
  (set! (.. js/window -onmousedown) myMouseMove)
  
  )




(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)
;;(.setInterval js/window #(tick) 30)
(.requestAnimationFrame js/window (fn [timestamp] (tick timestamp)))

        

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)



