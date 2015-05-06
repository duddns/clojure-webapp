(ns my-webapp.views
  (:require [my-webapp.db :as db]
            [clojure.string :as str]
            [hiccup.page :as hic-p]))

(defn gen-page-head
  [title]
  [:head
   [:title (str "Locations: " title)]
   (hic-p/include-css "/css/styles.css")])

(def header-links
  [:div#header-links
   "[ "
   [:a {:href "/"} "Home"]
   " | "
   [:a {:href "/add-location"} "Add a Location"]
   " | "
   [:a {:href "/all-locations"} "View All Locations"]
   " ]"])

(defn home-page
  []
  (hic-p/html5
    (gen-page-head "Home")
    header-links
    [:h1 "Home"]
    [:p "Webapp to store and display some 2D (x,y) locations."]))

(defn add-location-page
  []
  (hic-p/html5
    (gen-page-head "Add a Location")
    header-links
    [:h1 "Add a Location"]
    [:form {:action "/add-location" :method "POST"}
     [:p "x value: " [:input {:type "text" :name "x"}]]
     [:p "y value: " [:input {:type "text" :name "y"}]]
     [:p [:input {:type "submit" :value "submit location"}]]]))

(defn add-location-results-page
  [{:keys [x y]}]
  (let [id (db/add-location-to-db x y)]
    (hic-p/html5
      (gen-page-head "Added a Location")
      header-links
      [:h1 "Added a Location"]
      [:p "Added [" x ", " y "] (id: " id ") to the db. "
       [:a {:href (str "/location/" id)} "See for yourself"]
       "."])))

(defn update-location
  [{:keys [id x y]}]
  (do
    (db/update-location id x y)
    (hic-p/html5
      (gen-page-head "Updated a Location")
      header-links
      [:h1 "Updated a Location"]
      [:p "Updated (id: " id ", x: " x ", y: " y ")"]
      )))

(defn delete-location
  [{:keys [id]}]
  (do
    (db/delete-location id)
    (hic-p/html5
      (gen-page-head "Deleted a Location")
      header-links
      [:h1 "Deleted a Location"]
      [:p "Deleted (id: " id ") from the db. "]
      )))

(defn location-page
  [loc-id]
  (let [{x :x y :y} (db/get-xy loc-id)]
    (hic-p/html5
      (gen-page-head (str "Location " loc-id))
      header-links
      [:h1 "A Single Location"]
      [:p "id: " loc-id]
      [:p "x: " x]
      [:p "y: " y]
      [:form {:action "/update-location" :method "POST"}
       [:p "id: " [:input {:type "text" :name "id" :value loc-id :readonly "readonly"}]]
       [:p "x: " [:input {:type "text" :name "x"}]]
       [:p "y: " [:input {:type "text" :name "y"}]]
       [:p [:input {:type "submit" :value "update location"}]]])))

(defn all-locations-page
  []
  (let [all-locs (db/get-all-locations)]
    (hic-p/html5
      (gen-page-head "All Locations in the db")
      header-links
      [:h1 "All Locations"]
      [:table
       [:tr [:th "id"] [:th "x"] [:th "y"] [:th ""] [:th ""]]
       (for [loc all-locs]
         [:tr
          [:td (:id loc)]
          [:td (:x loc)]
          [:td (:y loc)]
          [:td
           [:a {:href (str "/location/" (:id loc))} "See Location Detail"]]
          [:td
           [:form {:action "/delete-location" :method "POST"}
            [:p
             [:input {:type "hidden" :name "id" :value (:id loc)}]
             [:input {:type "submit" :value "Delete Location"}]]]]])]
      )))