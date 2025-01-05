import axios from "axios";
import React, { useEffect, useState } from "react";

const HelloWorld: React.FC = () =>{
    const [message, setMessage] = useState<string>("");

    useEffect(() => {
        // Make the GET request to your Spring Boot API
        axios
          .get("http://localhost:8080/api/v1/hello")
          .then((response) => {
            setMessage(response.data); // Set the message to the response data
          })
          .catch((error) => {
            console.error("Error fetching message:", error);
            setMessage("Error connecting to server.");
          });
      }, []);

      return <div className="text-gray-600 mb-4">{message || "Loading..."}</div>;
}

export default HelloWorld;